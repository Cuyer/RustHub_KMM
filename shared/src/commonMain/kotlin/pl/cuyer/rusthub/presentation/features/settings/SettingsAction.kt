package pl.cuyer.rusthub.presentation.features.settings

import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.Language

sealed interface SettingsAction {
    data object OnChangePasswordClick : SettingsAction
    data object OnNotificationsClick : SettingsAction
    data object OnLogout : SettingsAction
    data object OnSubscriptionClick : SettingsAction
    data object OnDismissSubscriptionDialog : SettingsAction
    data object OnSubscribe : SettingsAction
    data object OnPrivacyPolicy : SettingsAction
    data object OnDeleteAccount : SettingsAction
    data object OnUpgradeAccount : SettingsAction
    data class OnThemeChange(val theme: Theme) : SettingsAction
    data class OnDynamicColorsChange(val enabled: Boolean) : SettingsAction
    data class OnLanguageChange(val language: Language) : SettingsAction
}
