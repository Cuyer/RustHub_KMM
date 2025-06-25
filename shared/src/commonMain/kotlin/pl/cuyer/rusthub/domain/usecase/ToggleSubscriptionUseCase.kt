package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.TimeoutException
import pl.cuyer.rusthub.domain.model.SubscriptionSyncOperation
import pl.cuyer.rusthub.domain.model.SyncState
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.util.SubscriptionSyncScheduler

class ToggleSubscriptionUseCase(
    private val serverDataSource: ServerDataSource,
    private val repository: SubscriptionRepository,
    private val syncDataSource: SubscriptionSyncDataSource,
    private val scheduler: SubscriptionSyncScheduler
) {
    operator fun invoke(serverId: Long, add: Boolean): Flow<Result<Unit>> = channelFlow {
        val flow = if (add) repository.addSubscription(serverId) else repository.removeSubscription(serverId)

        flow.collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    serverDataSource.updateSubscription(serverId, add)
                    syncDataSource.deleteOperation(serverId)
                    send(Result.Success(Unit))
                }
                is Result.Error -> {
                    when (result.exception) {
                        is NetworkUnavailableException, is TimeoutException -> {
                            serverDataSource.updateSubscription(serverId, add)
                            syncDataSource.upsertOperation(
                                SubscriptionSyncOperation(
                                    serverId,
                                    add,
                                    SyncState.PENDING
                                )
                            )
                            scheduler.schedule(serverId)
                            send(Result.Success(Unit))
                        }
                        else -> send(Result.Error(result.exception))
                    }
                }
                Result.Loading -> Unit
            }
        }
    }
}
