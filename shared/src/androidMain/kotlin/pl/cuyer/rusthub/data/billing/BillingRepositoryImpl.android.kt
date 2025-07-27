package pl.cuyer.rusthub.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import com.android.billingclient.api.queryProductDetails
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.domain.model.BillingErrorCode
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.PurchaseInfo
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

class BillingRepositoryImpl(context: Context) : BillingRepository {
    private val _purchaseFlow = MutableSharedFlow<PurchaseInfo>()
    override val purchaseFlow = _purchaseFlow.asSharedFlow()
    private val _errorFlow = MutableSharedFlow<BillingErrorCode>()
    override val errorFlow = _errorFlow.asSharedFlow()

    private data class ProductData(val details: ProductDetails, val offerToken: String?)

    private val productMap = mutableMapOf<String, ProductData>()

    private val billingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases(
            PendingPurchasesParams
                .newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.forEach { handlePurchase(it) }
            } else {
                _errorFlow.tryEmit(billingResult.responseCode.toErrorCode())
            }
        }
        .enableAutoServiceReconnection()
        .build()

    override fun queryProducts(ids: List<String>): Flow<List<BillingProduct>> = callbackFlow {
        // 1. Split IDs into SUBS and INAPP (by plan definitions)
        val subsPlans = SubscriptionPlan.entries.filter { it.basePlanId != null && ids.contains(it.basePlanId) }
        val inappPlans = SubscriptionPlan.entries.filter { it.basePlanId == null && ids.contains(it.productId) }

        val result = mutableListOf<BillingProduct>()

        // 2. Query SUBS products (subscriptions)
        if (subsPlans.isNotEmpty()) {
            val subsProducts = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(SubscriptionPlan.SUBSCRIPTION_ID)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val subsParams = QueryProductDetailsParams.newBuilder()
                .setProductList(subsProducts)
                .build()

            val subsResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(subsParams)
            }
            val subsList = mutableListOf<BillingProduct>()
            if (subsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                subsResult.productDetailsList?.forEach { pd ->
                    Napier.d(tag = "BillingRepository", message = "queryProducts: $pd")
                    pd.subscriptionOfferDetails?.forEach { offer ->
                        Napier.d(tag = "BillingRepository", message = "offer: ${offer.basePlanId}")
                        val planId = offer.basePlanId
                        if (subsPlans.any { it.basePlanId == planId }) {
                            productMap[planId] = ProductData(pd, offer.offerToken)
                            subsList.add(pd.toBillingProduct(planId, offer))
                        }
                    }
                }
            } else {
                _errorFlow.tryEmit(subsResult.billingResult.responseCode.toErrorCode())
            }
            result.addAll(subsList)
        }

        // 3. Query INAPP products (one-time purchases)
        if (inappPlans.isNotEmpty()) {
            val inappProducts = inappPlans.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it.productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }
            val inappParams = QueryProductDetailsParams.newBuilder()
                .setProductList(inappProducts)
                .build()

            val inappResult = withContext(Dispatchers.IO) {
                billingClient.queryProductDetails(inappParams)
            }
            val inappList = mutableListOf<BillingProduct>()
            if (inappResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                inappResult.productDetailsList?.forEach { pd ->
                    Napier.d(tag = "BillingRepository", message = "queryProducts: $pd")
                    val id = pd.productId
                    if (inappPlans.any { it.productId == id }) {
                        productMap[id] = ProductData(pd, null)
                        inappList.add(pd.toBillingProduct(id))
                    }
                }
            } else {
                _errorFlow.tryEmit(inappResult.billingResult.responseCode.toErrorCode())
            }
            result.addAll(inappList)
        }

        trySend(result)
        close()
        awaitClose {}
    }

    override fun launchBillingFlow(activity: Any, productId: String) {
        val act = activity as? Activity ?: return
        val data = productMap[productId] ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(data.details)
                        .apply { data.offerToken?.let { setOfferToken(it) } }
                        .build()
                )
            ).build()
        val result = billingClient.launchBillingFlow(act, params)
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _errorFlow.tryEmit(result.responseCode.toErrorCode())
        }
    }

    override fun getActiveSubscription(): Flow<ActiveSubscription?> = callbackFlow {
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        billingClient.queryPurchasesAsync(params) { billingResult, purchasesList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                val purchase = purchasesList.firstOrNull()
                val planId = purchase?.products?.firstOrNull()
                val plan = SubscriptionPlan.entries.firstOrNull { it.basePlanId == planId }

                val json = purchase?.originalJson
                val exp = try {
                    if (json != null) org.json.JSONObject(json).optLong("expiryTimeMillis", 0L) else 0L
                } catch (_: Exception) { 0L }

                val expiration = exp.takeIf { it > 0 }?.let { Instant.fromEpochMilliseconds(it) }

                trySend(plan?.let { ActiveSubscription(it, expiration) })
            } else {
                trySend(null)
            }

            close()
        }

        awaitClose {}
    }

    private fun handlePurchase(purchase: Purchase) {
        val product = purchase.products.firstOrNull() ?: return
        _purchaseFlow.tryEmit(PurchaseInfo(product, purchase.purchaseToken))
    }

    private fun ProductDetails.toBillingProduct(planId: String, offer: ProductDetails.SubscriptionOfferDetails): BillingProduct {
        val price = offer.pricingPhases.pricingPhaseList.firstOrNull()?.formattedPrice ?: ""
        return BillingProduct(
            id = planId,
            title = name,
            description = description,
            price = price
        )
    }

    private fun ProductDetails.toBillingProduct(productId: String): BillingProduct {
        val price = oneTimePurchaseOfferDetails?.formattedPrice ?: ""
        return BillingProduct(
            id = productId,
            title = name,
            description = description,
            price = price
        )
    }
}

private fun Int.toErrorCode(): BillingErrorCode {
    return when (this) {
        BillingClient.BillingResponseCode.USER_CANCELED -> BillingErrorCode.USER_CANCELED
        BillingClient.BillingResponseCode.SERVICE_UNAVAILABLE -> BillingErrorCode.SERVICE_UNAVAILABLE
        BillingClient.BillingResponseCode.BILLING_UNAVAILABLE -> BillingErrorCode.BILLING_UNAVAILABLE
        BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> BillingErrorCode.ITEM_UNAVAILABLE
        BillingClient.BillingResponseCode.DEVELOPER_ERROR -> BillingErrorCode.DEVELOPER_ERROR
        BillingClient.BillingResponseCode.ERROR -> BillingErrorCode.ERROR
        BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> BillingErrorCode.ITEM_ALREADY_OWNED
        BillingClient.BillingResponseCode.ITEM_NOT_OWNED -> BillingErrorCode.ITEM_NOT_OWNED
        BillingClient.BillingResponseCode.NETWORK_ERROR -> BillingErrorCode.NETWORK_ERROR
        BillingClient.BillingResponseCode.SERVICE_DISCONNECTED -> BillingErrorCode.SERVICE_DISCONNECTED
        BillingClient.BillingResponseCode.FEATURE_NOT_SUPPORTED -> BillingErrorCode.FEATURE_NOT_SUPPORTED
        BillingClient.BillingResponseCode.SERVICE_TIMEOUT -> BillingErrorCode.SERVICE_TIMEOUT
        else -> BillingErrorCode.UNKNOWN
    }
}
