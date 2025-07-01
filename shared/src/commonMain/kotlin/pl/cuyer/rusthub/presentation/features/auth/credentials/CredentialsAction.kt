package pl.cuyer.rusthub.presentation.features.auth.credentials

sealed interface CredentialsAction {
    data object OnSubmit : CredentialsAction
    data class OnEmailChange(val email: String) : CredentialsAction
    data class OnUsernameChange(val username: String) : CredentialsAction
    data class OnPasswordChange(val password: String) : CredentialsAction
}
