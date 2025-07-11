package pl.cuyer.rusthub.presentation.features.auth.password

sealed interface ResetPasswordAction {
    data object OnSend : ResetPasswordAction
    data class OnEmailChange(val email: String) : ResetPasswordAction
}
