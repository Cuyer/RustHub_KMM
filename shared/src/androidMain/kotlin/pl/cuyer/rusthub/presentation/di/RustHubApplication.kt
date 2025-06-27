package pl.cuyer.rusthub.presentation.di

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.work.CustomWorkerFactory

class RustHubApplication : Application(), Configuration.Provider {

    val repository by inject<FavouriteRepository>()
    val syncDataSource by inject<FavouriteSyncDataSource>()
    val subscriptionRepository by inject<SubscriptionRepository>()
    val subscriptionSyncDataSource by inject<SubscriptionSyncDataSource>()
    val serverDataSource by inject<ServerDataSource>()
    val tokenManager by inject<MessagingTokenManager>()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@RustHubApplication)
            modules(appModule, platformModule)
        }
        WorkManager.initialize(this, workManagerConfiguration)
    }

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(
                CustomWorkerFactory(
                    favouriteRepository = repository,
                    favouriteSyncDataSource = syncDataSource,
                    subscriptionRepository = subscriptionRepository,
                    subscriptionSyncDataSource = subscriptionSyncDataSource,
                    serverDataSource = serverDataSource,
                    tokenManager = tokenManager
                )
            )
            .build()
}
