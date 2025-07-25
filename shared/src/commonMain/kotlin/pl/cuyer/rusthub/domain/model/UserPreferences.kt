package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserPreferences(
    val themeConfig: Theme = Theme.SYSTEM,
    val useDynamicColor: Boolean = false,
    val useSystemColors: Boolean = true,
)
