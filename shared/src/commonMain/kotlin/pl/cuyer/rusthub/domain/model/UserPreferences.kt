package pl.cuyer.rusthub.domain.model

data class UserPreferences(
    val darkThemeConfig: Theme = Theme.SYSTEM,
    val useDynamicColor: Boolean = false
)
