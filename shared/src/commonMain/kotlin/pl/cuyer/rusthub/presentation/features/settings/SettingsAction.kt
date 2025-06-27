package pl.cuyer.rusthub.presentation.features.settings

import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme

sealed interface SettingsAction {
    data class OnThemeChange(val theme: Theme) : SettingsAction
    data class OnLanguageChange(val language: Language) : SettingsAction
    data object OnNotificationsClick : SettingsAction
    data object OnLogout : SettingsAction
}
