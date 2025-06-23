package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.FavouriteSyncOperation
import pl.cuyer.rusthub.domain.model.SyncState
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.util.SyncScheduler

class ToggleFavouriteUseCase(
    private val serverDataSource: ServerDataSource,
    private val repository: FavouriteRepository,
    private val syncDataSource: FavouriteSyncDataSource,
    private val scheduler: SyncScheduler
) {
    operator fun invoke(serverId: Long, add: Boolean): Flow<Result<Unit>> = channelFlow {
        repository.run { if (add) addFavourite(serverId) else removeFavourite(serverId) }
            .collectLatest { result ->
                when (result) {
                    is Result.Success -> {
                        serverDataSource.updateFavourite(serverId, add)
                        syncDataSource.deleteOperation(serverId)
                        send(Result.Success(Unit))
                    }
                    is Result.Error -> {
                        syncDataSource.upsertOperation(
                            FavouriteSyncOperation(serverId, add, SyncState.PENDING)
                        )
                        scheduler.schedule()
                        send(Result.Error(result.exception))
                    }
                    Result.Loading -> send(Result.Loading)
                }
            }
    }
}
