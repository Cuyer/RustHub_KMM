package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import io.github.aakira.napier.Napier
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.domain.exception.FavoriteLimitException
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.common.Result as DomainResult

class FavouriteSyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: FavouriteRepository,
    private val syncDataSource: FavouriteSyncDataSource,
    private val serverDataSource: ServerDataSource
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result = coroutineScope {
        val operations = syncDataSource.getPendingOperations()
        if (operations.isEmpty()) return@coroutineScope Result.success()

        val tasks = operations.map { operation ->
            async {
                var success = false
                var throwable: Throwable? = null
                repository.run {
                    if (operation.isAdd) addFavourite(operation.serverId)
                    else removeFavourite(operation.serverId)
                }.collectLatest { result ->
                    when (result) {
                        is DomainResult.Success -> {
                            serverDataSource.updateFavourite(operation.serverId, operation.isAdd)
                            syncDataSource.deleteOperation(operation.serverId)
                            success = true
                        }

                        is DomainResult.Error -> {
                            when (result.exception) {
                                is FavoriteLimitException -> {
                                    syncDataSource.deleteOperation(operation.serverId)
                                    success = true
                                }
                                else -> {
                                    throwable = result.exception
                                    success = false
                                }
                            }
                        }

                    }
                }
                if (success) null else Pair(operation, throwable)
            }
        }

        val results = tasks.awaitAll().filterNotNull()
        return@coroutineScope if (results.isNotEmpty()) {
            results.forEach { (operation, throwable) ->
                Napier.e(
                    "Failed to sync favourite ${if (operation.isAdd) "add" else "remove"} " +
                        "for server ${operation.serverId}",
                    throwable
                )
            }
            Result.retry()
        } else Result.success()
    }
}
