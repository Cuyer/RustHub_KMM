package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.domain.model.PurchaseSyncOperation
import pl.cuyer.rusthub.domain.model.SyncState
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.TimeoutException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.util.PurchaseSyncScheduler

class ConfirmPurchaseUseCase(
    private val repository: PurchaseRepository,
    private val syncDataSource: PurchaseSyncDataSource,
    private val scheduler: PurchaseSyncScheduler
) {
    operator fun invoke(token: String): Flow<Result<Unit>> = channelFlow {
        repository.confirmPurchase(token).collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    syncDataSource.deleteOperation(token)
                    send(Result.Success(Unit))
                }
                is Result.Error -> {
                    when (result.exception) {
                        is NetworkUnavailableException, is TimeoutException,
                        is ServiceUnavailableException -> {
                            syncDataSource.upsertOperation(
                                PurchaseSyncOperation(token, SyncState.PENDING)
                            )
                            scheduler.schedule()
                        }
                    }
                    send(Result.Error(result.exception))
                }
            }
        }
    }
}
