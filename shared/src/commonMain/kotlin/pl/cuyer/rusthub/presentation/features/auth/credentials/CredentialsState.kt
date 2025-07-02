package pl.cuyer.rusthub.presentation.features.auth.credentials

import pl.cuyer.rusthub.domain.model.AuthProvider

data class CredentialsState(
    val email: String,
    val userExists: Boolean,
    val provider: AuthProvider? = null,
    val username: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)
