package pl.cuyer.rusthub.domain.repository.purchase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.ActiveSubscription

interface PurchaseRepository {
    fun confirmPurchase(token: String, productId: String? = null): Flow<Result<Unit>>
    fun getActiveSubscription(obfuscatedId: String): Flow<Result<ActiveSubscription?>>
}
