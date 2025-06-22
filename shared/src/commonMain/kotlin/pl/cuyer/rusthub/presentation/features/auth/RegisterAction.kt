package pl.cuyer.rusthub.presentation.features.auth

sealed interface RegisterAction {
    data object OnRegister : RegisterAction

    data class OnEmailChange(val email: String) : RegisterAction
    data class OnPasswordChange(val password: String) : RegisterAction
    data class OnUsernameChange(val username: String) : RegisterAction
}
