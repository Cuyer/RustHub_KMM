package pl.cuyer.rusthub.presentation.features.auth.credentials

data class CredentialsState(
    val email: String,
    val userExists: Boolean,
    val username: String = "",
    val password: String = "",
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)
