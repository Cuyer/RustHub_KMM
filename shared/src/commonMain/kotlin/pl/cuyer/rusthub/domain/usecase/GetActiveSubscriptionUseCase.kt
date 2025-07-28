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
    operator fun invoke(): Flow<ActiveSubscription?> = channelFlow {
        CrashReporter.log("Fetching active subscription")
        Napier.d("Fetching active subscription", tag = "SubscriptionUseCase")

        val id = getUserUseCase().first()?.obfuscatedId
        Napier.d("User id: $id", tag = "SubscriptionUseCase")
        if (id == null) {
            send(null)
            return@channelFlow
        }
        repository.getActiveSubscription(id).collectLatest { result ->
            CrashReporter.log("Subscription result: $result")
            when (result) {
                is Result.Success -> send(result.data)
                is Result.Error -> {
                    CrashReporter.recordException(result.exception)
                    send(null)
                }
            }
        }
    }
}
