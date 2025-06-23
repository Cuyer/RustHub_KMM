package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.collect
import org.koin.core.context.GlobalContext
import pl.cuyer.rusthub.common.Result as DomainResult
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class FavouriteSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        val koin = GlobalContext.get().koin
        val repository: FavouriteRepository = koin.get()
        val syncDataSource: FavouriteSyncDataSource = koin.get()
        val serverDataSource: ServerDataSource = koin.get()

        val operations = syncDataSource.getPendingOperations()
        if (operations.isEmpty()) return Result.success()

        operations.forEach { operation ->
            var success = false
            repository.run {
                if (operation.isAdd) addFavourite(operation.serverId)
                else removeFavourite(operation.serverId)
            }.collect { result ->
                when (result) {
                    is DomainResult.Success -> {
                        serverDataSource.updateFavourite(operation.serverId, operation.isAdd)
                        syncDataSource.deleteOperation(operation.serverId)
                        success = true
                    }
                    is DomainResult.Error -> success = false
                    DomainResult.Loading -> {}
                }
            }
            if (!success) return Result.retry()
        }

        return Result.success()
    }
}
