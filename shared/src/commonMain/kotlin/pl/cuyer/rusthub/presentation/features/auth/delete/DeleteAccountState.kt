package pl.cuyer.rusthub.presentation.features.auth.delete

import pl.cuyer.rusthub.domain.model.AuthProvider

data class DeleteAccountState(
    val username: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null,
    val provider: AuthProvider? = null,
    val isLoading: Boolean = false
)
