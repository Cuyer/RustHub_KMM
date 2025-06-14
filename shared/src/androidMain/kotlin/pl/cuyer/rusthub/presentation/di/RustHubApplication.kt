package pl.cuyer.rusthub.presentation.di

import android.app.Application
import org.koin.android.ext.koin.androidContext

class RustHubApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidContext(this@RustHubApplication)
            modules(appModule, platformModule)
        }
    }
}