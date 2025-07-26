package pl.cuyer.rusthub.domain.repository.purchase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result

interface PurchaseRepository {
    fun confirmPurchase(token: String): Flow<Result<Unit>>
}
