package pl.cuyer.rusthub.util

import kotlinx.coroutines.flow.Flow

expect class SystemDarkThemeObserver {
    val isSystemDarkTheme: Flow<Boolean>
}
