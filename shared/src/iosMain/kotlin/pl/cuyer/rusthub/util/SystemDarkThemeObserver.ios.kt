package pl.cuyer.rusthub.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class SystemDarkThemeObserver {
    actual val isSystemDarkTheme: Flow<Boolean> = flowOf(false)
}
