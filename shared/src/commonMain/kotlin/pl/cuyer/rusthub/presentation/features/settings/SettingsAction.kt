package pl.cuyer.rusthub.presentation.features.settings

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
}
