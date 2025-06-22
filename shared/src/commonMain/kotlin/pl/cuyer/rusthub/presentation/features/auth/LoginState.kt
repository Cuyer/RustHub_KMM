package pl.cuyer.rusthub.presentation.features.auth

/** State for the login screen */
data class LoginState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null
)
