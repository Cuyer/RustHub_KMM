package pl.cuyer.rusthub.util

import android.content.ComponentCallbacks
import android.content.Context
import android.content.res.Configuration
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

private val Configuration.isSystemInDarkTheme
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

actual class SystemDarkThemeObserver(private val context: Context) {
    actual val isSystemDarkTheme: Flow<Boolean> = callbackFlow {
        trySend(context.resources.configuration.isSystemInDarkTheme)
        val callbacks = object : ComponentCallbacks {
            override fun onConfigurationChanged(newConfig: Configuration) {
                trySend(newConfig.isSystemInDarkTheme)
            }

            override fun onLowMemory() {}
        }
        context.registerComponentCallbacks(callbacks)
        awaitClose { context.unregisterComponentCallbacks(callbacks) }
    }.distinctUntilChanged().conflate()
}
