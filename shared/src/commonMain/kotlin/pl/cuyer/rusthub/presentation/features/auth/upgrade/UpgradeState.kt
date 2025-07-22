package pl.cuyer.rusthub.presentation.features.auth.upgrade

import androidx.compose.runtime.Immutable

@Immutable
data class UpgradeState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
    val googleLoading: Boolean = false,
)
