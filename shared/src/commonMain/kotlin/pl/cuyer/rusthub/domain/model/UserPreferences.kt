package pl.cuyer.rusthub.domain.model

data class UserPreferences(
    val themeConfig: Theme = Theme.SYSTEM,
    val useDynamicColor: Boolean = false
)
