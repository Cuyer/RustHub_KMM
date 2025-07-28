package pl.cuyer.rusthub.data.billing

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.domain.model.BillingProduct
import pl.cuyer.rusthub.domain.model.PurchaseInfo
import pl.cuyer.rusthub.domain.model.BillingErrorCode
import pl.cuyer.rusthub.domain.repository.purchase.BillingRepository
import kotlinx.coroutines.flow.flowOf

class BillingRepositoryImpl : BillingRepository {
    override val purchaseFlow: Flow<PurchaseInfo> = emptyFlow()
    override val errorFlow: Flow<BillingErrorCode> = emptyFlow()
    override fun queryProducts(ids: List<String>): Flow<List<BillingProduct>> = flowOf(emptyList())
    override fun launchBillingFlow(activity: Any, productId: String, obfuscatedId: String?) {}
}
