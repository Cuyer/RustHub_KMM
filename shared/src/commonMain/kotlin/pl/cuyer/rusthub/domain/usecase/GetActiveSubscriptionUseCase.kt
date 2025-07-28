package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository

class GetActiveSubscriptionUseCase(
    private val repository: PurchaseRepository,
    private val getUserUseCase: GetUserUseCase
) {
    operator fun invoke(): Flow<ActiveSubscription?> = channelFlow {
        val id = getUserUseCase().first()?.obfuscatedId
        if (id == null) {
            send(null)
            return@channelFlow
        }
        repository.getActiveSubscription(id).collectLatest { result ->
            when (result) {
                is Result.Success -> send(result.data)
                is Result.Error -> send(null)
            }
        }
    }
}
