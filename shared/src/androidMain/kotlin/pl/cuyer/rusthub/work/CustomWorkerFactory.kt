package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.domain.repository.item.ItemRepository
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource

class CustomWorkerFactory(
    private val favouriteRepository: FavouriteRepository,
    private val favouriteSyncDataSource: FavouriteSyncDataSource,
    private val subscriptionRepository: SubscriptionRepository,
    private val subscriptionSyncDataSource: SubscriptionSyncDataSource,
    private val serverDataSource: ServerDataSource,
    private val tokenManager: MessagingTokenManager,
    private val itemRepository: ItemRepository,
    private val itemDataSource: ItemDataSource,
    private val itemSyncDataSource: pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
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
                    favouriteRepository,
                    favouriteSyncDataSource,
                    serverDataSource
                )
            }
            SubscriptionSyncWorker::class.qualifiedName -> {
                SubscriptionSyncWorker(
                    appContext,
                    workerParameters,
                    subscriptionRepository,
                    subscriptionSyncDataSource,
                    serverDataSource
                )
            }
            ItemsWorker::class.qualifiedName -> {
                ItemsWorker(
                    appContext,
                    workerParameters,
                    itemRepository,
                    itemDataSource,
                    itemSyncDataSource
                )
            }
            TokenRefreshWorker::class.qualifiedName -> {
                TokenRefreshWorker(appContext, workerParameters, tokenManager)
            }
            else -> null
        }
    }
}
