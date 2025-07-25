package pl.cuyer.rusthub.presentation.features.auth.credentials

import androidx.compose.runtime.Immutable

@Immutable
sealed interface CredentialsAction {
    @Immutable
    data object OnSubmit : CredentialsAction
    @Immutable
    data class OnUsernameChange(val username: String) : CredentialsAction
    @Immutable
    data class OnPasswordChange(val password: String) : CredentialsAction
    @Immutable
    data object OnForgotPassword : CredentialsAction
    @Immutable
    data object OnNavigateUp : CredentialsAction
    @Immutable
    data object OnGoogleLogin : CredentialsAction
}
