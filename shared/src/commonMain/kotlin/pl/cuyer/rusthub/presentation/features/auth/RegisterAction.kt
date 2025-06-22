package pl.cuyer.rusthub.presentation.features.auth

sealed interface RegisterAction {
    data class OnRegister(val email: String, val password: String, val username: String) :
        RegisterAction
}