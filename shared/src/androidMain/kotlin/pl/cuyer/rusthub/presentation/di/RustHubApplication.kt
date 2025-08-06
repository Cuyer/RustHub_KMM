package pl.cuyer.rusthub.presentation.di

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.os.StrictMode
import androidx.work.Configuration
import androidx.work.WorkManager
import com.appmattus.certificatetransparency.BasicAndroidCTLogger
import com.appmattus.certificatetransparency.installCertificateTransparencyProvider
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import pl.cuyer.rusthub.BuildConfig
import pl.cuyer.rusthub.data.local.DatabasePassphraseProvider
import pl.cuyer.rusthub.util.NotificationPresenter
import pl.cuyer.rusthub.work.CustomWorkerFactory

private fun needCtProvider(): Boolean {
    return Build.VERSION.SDK_INT < 36
}

class RustHubApplication : Application(), Configuration.Provider {

    val koinReady = CompletableDeferred<Unit>()
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var workConfig: Configuration

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
            )
            StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                    .detectAll()
                    .penaltyLog()
                    .build(),
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

        applicationScope.launch {
            val passphrase = DatabasePassphraseProvider(this@RustHubApplication).loadPassphrase()
            initKoin(passphrase) {
                androidContext(this@RustHubApplication)
                if (BuildConfig.DEBUG) {
                    androidLogger()
                }
            }

            workConfig = Configuration.Builder()
                .setWorkerFactory(
                    CustomWorkerFactory(
                        favouriteRepository = get(),
                        favouriteSyncDataSource = get(),
                        subscriptionRepository = get(),
                        subscriptionSyncDataSource = get(),
                        serverDataSource = get(),
                        tokenManager = get(),
                        itemRepository = get(),
                        itemDataSource = get(),
                        itemSyncDataSource = get(),
                        monumentRepository = get(),
                        monumentDataSource = get(),
                        monumentSyncDataSource = get(),
                        purchaseRepository = get(),
                        purchaseSyncDataSource = get(),
                        userRepository = get(),
                        authDataSource = get(),
                    ),
                )
                .build()

            NotificationPresenter(this@RustHubApplication).createDefaultChannels()
            WorkManager.initialize(this@RustHubApplication, workManagerConfiguration)
            koinReady.complete(Unit)
        }
    }

    override val workManagerConfiguration: Configuration
        get() = if (::workConfig.isInitialized) {
            workConfig
        } else {
            Configuration.Builder().build()
        }
}
