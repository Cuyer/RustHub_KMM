package pl.cuyer.rusthub.presentation.features.auth.upgrade

sealed interface UpgradeAction {
    data object OnSubmit : UpgradeAction
    data class OnEmailChange(val email: String) : UpgradeAction
    data class OnUsernameChange(val username: String) : UpgradeAction
    data class OnPasswordChange(val password: String) : UpgradeAction
    data object OnNavigateUp : UpgradeAction
    data object OnGoogleLogin : UpgradeAction
}
