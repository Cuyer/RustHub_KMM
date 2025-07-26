package pl.cuyer.rusthub.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.PurchaseInfo
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository

class BillingRepositoryImpl(context: Context) : BillingRepository {
    private val _purchaseFlow = MutableSharedFlow<PurchaseInfo>()
    override val purchaseFlow = _purchaseFlow.asSharedFlow()

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

            val subsResult = suspendCancellableCoroutine<List<BillingProduct>> { cont ->
                billingClient.queryProductDetailsAsync(subsParams) { billingResult, response ->
                    val list = mutableListOf<BillingProduct>()
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        response.productDetailsList.forEach { pd ->
                            Napier.d(tag = "BillingRepository", message = "queryProducts: $pd")
                            pd.subscriptionOfferDetails?.forEach { offer ->
                                Napier.d(tag = "BillingRepository", message = "offer: ${offer.basePlanId}")
                                val planId = offer.basePlanId
                                if (subsPlans.any { it.basePlanId == planId }) {
                                    productMap[planId] = ProductData(pd, offer.offerToken)
                                    list.add(pd.toBillingProduct(planId, offer))
                                }
                            }
                        }
                    }
                    cont.resume(list) { cause, _, _ -> }
                }
            }
            result.addAll(subsResult)
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

            val inappResult = suspendCancellableCoroutine<List<BillingProduct>> { cont ->
                billingClient.queryProductDetailsAsync(inappParams) { billingResult, response ->
                    val list = mutableListOf<BillingProduct>()
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        response.productDetailsList.forEach { pd ->
                            Napier.d(tag = "BillingRepository", message = "queryProducts: $pd")
                            val id = pd.productId
                            if (inappPlans.any { it.productId == id }) {
                                productMap[id] = ProductData(pd, null)
                                list.add(pd.toBillingProduct(id))
                            }
                        }
                    }
                    cont.resume(list) { cause, _, _ -> }
                }
            }
            result.addAll(inappResult)
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
        billingClient.launchBillingFlow(act, params)
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
