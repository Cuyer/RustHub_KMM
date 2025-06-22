package pl.cuyer.rusthub.presentation.features.auth

/** Actions from the login screen */
sealed interface LoginAction {
    data object OnLogin : LoginAction
    data class OnUsernameChange(val username: String) : LoginAction
    data class OnPasswordChange(val password: String) : LoginAction
}
