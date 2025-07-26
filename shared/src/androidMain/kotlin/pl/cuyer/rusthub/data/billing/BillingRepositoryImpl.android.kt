package pl.cuyer.rusthub.data.billing

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.QueryProductDetailsParams
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.PurchaseInfo
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository

class BillingRepositoryImpl(context: Context) : BillingRepository {
    private val _purchaseFlow = MutableSharedFlow<PurchaseInfo>()
    override val purchaseFlow = _purchaseFlow.asSharedFlow()

    private val productMap = mutableMapOf<String, ProductDetails>()

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
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(ids.map {
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(it)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            }).build()
        billingClient.queryProductDetailsAsync(params) { result, details ->
            if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                details.productDetailsList.forEach { productMap[it.productId] = it }
                trySend(details.productDetailsList.map { it.toBillingProduct() })
            }
            close()
        }
        awaitClose {}
    }

    override fun launchBillingFlow(activity: Any, productId: String) {
        val act = activity as? Activity ?: return
        val details = productMap[productId] ?: return
        val offer = details.subscriptionOfferDetails?.firstOrNull() ?: return
        val params = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offer.offerToken)
                        .build()
                )
            ).build()
        billingClient.launchBillingFlow(act, params)
    }

    private fun handlePurchase(purchase: Purchase) {
        val product = purchase.products.firstOrNull() ?: return
        _purchaseFlow.tryEmit(PurchaseInfo(product, purchase.purchaseToken))
    }

    private fun ProductDetails.toBillingProduct(): BillingProduct {
        val offer = subscriptionOfferDetails?.firstOrNull()
        val price = offer?.pricingPhases?.pricingPhaseList?.firstOrNull()?.formattedPrice ?: ""
        return BillingProduct(
            id = productId,
            title = name,
            description = description,
            price = price
        )
    }
}
