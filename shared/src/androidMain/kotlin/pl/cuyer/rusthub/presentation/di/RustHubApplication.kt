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
import org.koin.dsl.module
import pl.cuyer.rusthub.data.local.DatabasePassphraseProvider
import pl.cuyer.rusthub.SharedBuildConfig
import pl.cuyer.rusthub.util.ActivityProvider
import pl.cuyer.rusthub.util.NotificationPresenter
import pl.cuyer.rusthub.work.CustomWorkerFactory

private fun needCtProvider(): Boolean {
    return Build.VERSION.SDK_INT < 36
}

class RustHubApplication : Application(), Configuration.Provider {

    val koinReady = CompletableDeferred<Unit>()
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private lateinit var workConfig: Configuration
    private lateinit var activityProvider: ActivityProvider

    @SuppressLint("MissingPermission")
    override fun onCreate() {
        super.onCreate()
        if (SharedBuildConfig.IS_DEBUG_BUILD) {
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
                logger = BasicAndroidCTLogger(SharedBuildConfig.IS_DEBUG_BUILD)
            }
        }
        val factory = if (SharedBuildConfig.IS_DEBUG_BUILD) {
            DebugAppCheckProviderFactory.getInstance()
        } else {
            PlayIntegrityAppCheckProviderFactory.getInstance()
        }
        FirebaseAppCheck.getInstance().installAppCheckProviderFactory(factory)

        activityProvider = ActivityProvider(this)
        applicationScope.launch {
            val passphrase = DatabasePassphraseProvider(this@RustHubApplication).loadPassphrase()
            initKoin(passphrase) {
                androidContext(this@RustHubApplication)
                if (SharedBuildConfig.IS_DEBUG_BUILD) {
                    androidLogger()
                }
                modules(module { single { activityProvider } })
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
            try {
                WorkManager.getInstance(this@RustHubApplication)
            } catch (e: IllegalStateException) {
                WorkManager.initialize(this@RustHubApplication, workManagerConfiguration)
            }
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
