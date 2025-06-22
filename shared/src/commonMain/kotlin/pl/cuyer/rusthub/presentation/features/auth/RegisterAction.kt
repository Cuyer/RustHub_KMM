package pl.cuyer.rusthub.presentation.features.auth

sealed interface RegisterAction {
    data class OnRegister(val email: String, val password: String, val username: String) :
        RegisterAction

    data class OnUpdateEmail(val email: String) : RegisterAction
    data class OnUpdatePassword(val password: String) : RegisterAction
    data class OnUpdateUsername(val username: String) : RegisterAction
}