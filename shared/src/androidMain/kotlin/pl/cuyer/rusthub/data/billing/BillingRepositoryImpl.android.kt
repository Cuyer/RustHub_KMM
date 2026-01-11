package pl.cuyer.rusthub.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.domain.model.BillingErrorCode
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.PurchaseInfo
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

class BillingRepositoryImpl(context: Context) : BillingRepository {
    private val _purchaseFlow = MutableSharedFlow<PurchaseInfo>(replay = 1)
    override val purchaseFlow = _purchaseFlow.asSharedFlow()
    private val _errorFlow = MutableSharedFlow<BillingErrorCode>(replay = 1)
    override val errorFlow = _errorFlow.asSharedFlow()

    private data class ProductData(val details: ProductDetails, val offerToken: String?)

    private val productMap = mutableMapOf<String, ProductData>()

    private val billingClient = BillingClient.newBuilder(context)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder().enableOneTimeProducts().build()
        )
        .setListener { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.forEach {
                    handlePurchase(it)
                }
            } else {
                _errorFlow.tryEmit(billingResult.responseCode.toErrorCode())
            }
        }
        .enableAutoServiceReconnection()
        .build()

    override fun queryProducts(ids: List<String>): Flow<List<BillingProduct>> = callbackFlow {
        val subsPlans = SubscriptionPlan.entries.filter { it.basePlanId != null && ids.contains(it.basePlanId) }
        val inappPlans = SubscriptionPlan.entries.filter { it.basePlanId == null && ids.contains(it.productId) }

        val result = mutableListOf<BillingProduct>()

        if (subsPlans.isNotEmpty()) {
            val subsProducts = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(SubscriptionPlan.SUBSCRIPTION_ID)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val subsParams = QueryProductDetailsParams.newBuilder().setProductList(subsProducts).build()

            val subsResult = withContext(Dispatchers.IO) { billingClient.queryProductDetails(subsParams) }

            val subsList = mutableListOf<BillingProduct>()
            if (subsResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                subsResult.productDetailsList?.forEach { pd ->
                    pd.subscriptionOfferDetails
                        ?.groupBy { it.basePlanId }
                        ?.forEach { (planId, offers) ->
                            if (subsPlans.any { it.basePlanId == planId }) {
                                val offer = offers.selectBestOffer() ?: return@forEach
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

        if (inappPlans.isNotEmpty()) {
            val inappProducts = inappPlans.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it.productId)
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()
            }
            val inappParams = QueryProductDetailsParams.newBuilder().setProductList(inappProducts).build()

            val inappResult = withContext(Dispatchers.IO) { billingClient.queryProductDetails(inappParams) }

            val inappList = mutableListOf<BillingProduct>()
            if (inappResult.billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                inappResult.productDetailsList?.forEach { pd ->
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

    override fun launchBillingFlow(activity: Any, productId: String, obfuscatedId: String?) {
        val act = activity as? Activity ?: return
        val data = productMap[productId] ?: run {
            _errorFlow.tryEmit(BillingErrorCode.ITEM_UNAVAILABLE)
            return
        }
        if (!billingClient.isReady) {
            _errorFlow.tryEmit(BillingErrorCode.SERVICE_DISCONNECTED)
            return
        }
        val productParams = BillingFlowParams.ProductDetailsParams.newBuilder()
            .setProductDetails(data.details)
        if (data.details.productType == BillingClient.ProductType.SUBS) {
            val token = data.offerToken ?: run {
                _errorFlow.tryEmit(BillingErrorCode.DEVELOPER_ERROR)
                return
            }
            productParams.setOfferToken(token)
        }
        val paramsBuilder = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(listOf(productParams.build()))
        obfuscatedId?.let { paramsBuilder.setObfuscatedAccountId(it) }
        val result = billingClient.launchBillingFlow(act, paramsBuilder.build())
        if (result.responseCode != BillingClient.BillingResponseCode.OK) {
            _errorFlow.tryEmit(result.responseCode.toErrorCode())
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun clearPurchaseCache() {
        _purchaseFlow.resetReplayCache()
    }


    private fun handlePurchase(purchase: Purchase) {
        val product = purchase.products.firstOrNull()
        if (product == null) {
            return
        }
        _purchaseFlow.tryEmit(PurchaseInfo(product, purchase.purchaseToken))
    }

    private fun ProductDetails.toBillingProduct(
        planId: String,
        offer: ProductDetails.SubscriptionOfferDetails
    ): BillingProduct {
        val phases = offer.pricingPhases.pricingPhaseList
        val paidPhase = phases.firstOrNull { it.priceAmountMicros > 0L }
        val price = paidPhase?.formattedPrice ?: phases.firstOrNull()?.formattedPrice ?: ""
        val hasFreeTrial = phases.any { it.isFreeTrialPhase() }
        return BillingProduct(
            id = planId,
            title = name,
            description = description,
            price = price,
            hasFreeTrial = hasFreeTrial
        )
    }

    private fun ProductDetails.toBillingProduct(productId: String): BillingProduct {
        val price = oneTimePurchaseOfferDetails?.formattedPrice ?: ""
        return BillingProduct(id = productId, title = name, description = description, price = price)
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
        else -> BillingErrorCode.UNKNOWN
    }
}

private fun List<ProductDetails.SubscriptionOfferDetails>.selectBestOffer(): ProductDetails.SubscriptionOfferDetails? {
    return sortedWith(compareByDescending { it.hasFreeTrial() }).firstOrNull()
}

private fun ProductDetails.SubscriptionOfferDetails.hasFreeTrial(): Boolean {
    return pricingPhases.pricingPhaseList.any { it.isFreeTrialPhase() }
}

private fun ProductDetails.PricingPhase.isFreeTrialPhase(): Boolean {
    return priceAmountMicros == 0L && billingPeriod.isNotBlank()
}
