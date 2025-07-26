package pl.cuyer.rusthub.presentation.di

import android.app.Application
import androidx.work.Configuration
import androidx.work.WorkManager
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.favourite.network.FavouriteRepository
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.item.ItemRepository
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.util.NotificationPresenter
import pl.cuyer.rusthub.work.CustomWorkerFactory
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import pl.cuyer.rusthub.BuildConfig
import android.os.Build
import android.os.StrictMode
import com.appmattus.certificatetransparency.installCertificateTransparencyProvider
import com.appmattus.certificatetransparency.BasicAndroidCTLogger

private fun needCtProvider(): Boolean {
    return Build.VERSION.SDK_INT < 36
}

class RustHubApplication : Application(), Configuration.Provider {

    val repository by inject<FavouriteRepository>()
    val syncDataSource by inject<FavouriteSyncDataSource>()
    val subscriptionRepository by inject<SubscriptionRepository>()
    val subscriptionSyncDataSource by inject<SubscriptionSyncDataSource>()
    val serverDataSource by inject<ServerDataSource>()
    val tokenManager by inject<MessagingTokenManager>()
    val itemRepository by inject<ItemRepository>()
    val itemDataSource by inject<ItemDataSource>()
    val itemSyncDataSource by inject<ItemSyncDataSource>()
    val purchaseRepository by inject<pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository>()
    val purchaseSyncDataSource by inject<pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource>()

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build()
            )
        }
        if (needCtProvider()) {
            installCertificateTransparencyProvider {
                logger = BasicAndroidCTLogger(BuildConfig.DEBUG)
            }
        }
        val factory = if (BuildConfig.DEBUG) {
            DebugAppCheckProviderFactory.getInstance()
        } else {
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)
        initKoin {
            androidContext(this@RustHubApplication)
            if (BuildConfig.DEBUG) {
                androidLogger()
            }
            modules(appModule, platformModule)
        }
        NotificationPresenter(this).createDefaultChannels()
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
                    tokenManager = tokenManager,
                    itemRepository = itemRepository,
                    itemDataSource = itemDataSource,
                    itemSyncDataSource = itemSyncDataSource,
                    purchaseRepository = purchaseRepository,
                    purchaseSyncDataSource = purchaseSyncDataSource
                )
            )
            .build()
}

