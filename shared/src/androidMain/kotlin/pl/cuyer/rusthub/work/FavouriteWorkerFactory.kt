package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class FavouriteWorkerFactory(
    private val repository: FavouriteRepository,
    private val syncDataSource: FavouriteSyncDataSource,
    private val serverDataSource: ServerDataSource
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            FavouriteSyncWorker::class.qualifiedName -> {
                FavouriteSyncWorker(
                    appContext,
                    workerParameters,
                    repository,
                    syncDataSource,
                    serverDataSource
                )
            }

            else -> null
        }
    }
}