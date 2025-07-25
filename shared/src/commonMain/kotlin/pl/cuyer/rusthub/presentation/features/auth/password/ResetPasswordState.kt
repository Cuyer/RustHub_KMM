package pl.cuyer.rusthub.presentation.features.auth.password

import androidx.compose.runtime.Immutable

@Immutable
data class ResetPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false
)
