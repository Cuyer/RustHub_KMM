package pl.cuyer.rusthub.presentation.features.auth.password

data class ResetPasswordState(
    val email: String = "",
    val emailError: String? = null,
    val isLoading: Boolean = false
)
