package pl.cuyer.rusthub.presentation.features.auth.upgrade

data class UpgradeState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false,
)
