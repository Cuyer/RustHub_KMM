package pl.cuyer.rusthub.presentation.features.auth.confirm

import pl.cuyer.rusthub.domain.model.AuthProvider

data class ConfirmEmailState(
    val email: String = "",
    val provider: AuthProvider? = null,
    val isLoading: Boolean = false,
)
