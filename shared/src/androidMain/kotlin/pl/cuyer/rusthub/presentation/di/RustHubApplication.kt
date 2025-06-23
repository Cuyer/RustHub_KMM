package pl.cuyer.rusthub.presentation.di

import android.app.Application
import androidx.work.Configuration
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.work.FavouriteWorkerFactory

class RustHubApplication : Application(), Configuration.Provider {

    val repository by inject<FavouriteRepository>()
    val syncDataSource by inject<FavouriteSyncDataSource>()
    val serverDataSource by inject<ServerDataSource>()

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@RustHubApplication)
            modules(appModule, platformModule)
        }
    }

    override val workManagerConfiguration: Configuration
        get() {
            return Configuration.Builder()
                .setWorkerFactory(
                    FavouriteWorkerFactory(
                        repository = repository,
                        syncDataSource = syncDataSource,
                        serverDataSource = serverDataSource
                    )
                )
                .build()
        }

}