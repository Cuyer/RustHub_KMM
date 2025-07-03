package pl.cuyer.rusthub.presentation.features.auth.password

sealed interface ChangePasswordAction {
    data object OnChange : ChangePasswordAction
    data class OnOldPasswordChange(val password: String) : ChangePasswordAction
    data class OnNewPasswordChange(val password: String) : ChangePasswordAction
}
