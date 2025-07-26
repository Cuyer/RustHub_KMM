package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository

class ConfirmPurchaseUseCase(
    private val repository: PurchaseRepository
) {
    operator fun invoke(token: String): Flow<Result<Unit>> = repository.confirmPurchase(token)
}
