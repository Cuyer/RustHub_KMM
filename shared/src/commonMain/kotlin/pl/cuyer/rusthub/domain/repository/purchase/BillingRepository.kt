package pl.cuyer.rusthub.domain.repository.purchase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.BillingErrorCode
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.PurchaseInfo

interface BillingRepository {
    val purchaseFlow: Flow<PurchaseInfo>
    val errorFlow: Flow<BillingErrorCode>
    fun queryProducts(ids: List<String>): Flow<List<BillingProduct>>
    fun launchBillingFlow(activity: Any, productId: String, obfuscatedId: String?)
    fun clearPurchaseCache()
}
