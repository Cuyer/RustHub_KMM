package pl.cuyer.rusthub.presentation.features.settings

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.Language

@Immutable
sealed interface SettingsAction {
    @Immutable
    data object OnChangePasswordClick : SettingsAction
    @Immutable
    data object OnNotificationsClick : SettingsAction
    @Immutable
    data object OnLogout : SettingsAction
    @Immutable
    data object OnSubscriptionClick : SettingsAction
    @Immutable
    data object OnDismissSubscriptionDialog : SettingsAction
    @Immutable
    data object OnSubscribe : SettingsAction
    @Immutable
    data object OnPrivacyPolicy : SettingsAction
    @Immutable
    data object OnDeleteAccount : SettingsAction
    @Immutable
    data object OnUpgradeAccount : SettingsAction
    @Immutable
    data class OnThemeChange(val theme: Theme) : SettingsAction
    @Immutable
    data class OnDynamicColorsChange(val enabled: Boolean) : SettingsAction
    @Immutable
    data class OnLanguageChange(val language: Language) : SettingsAction
}
