package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository

class GetActiveSubscriptionUseCase(
    private val repository: PurchaseRepository,
    private val getUserUseCase: GetUserUseCase
) {
    operator fun invoke(): Flow<Result<ActiveSubscription?>> = channelFlow {
        val id = getUserUseCase().first()?.obfuscatedId
        if (id == null) {
            send(Result.Success(null))
            return@channelFlow
        }
        repository.getActiveSubscription(id).collectLatest { result ->
            send(result)
        }
    }
}
