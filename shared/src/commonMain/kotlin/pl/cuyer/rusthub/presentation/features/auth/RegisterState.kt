package pl.cuyer.rusthub.presentation.features.auth

data class RegisterState(
    val isLoading: Boolean = false,
    val username: String = "",
    val password: String = "",
    val email: String = "",
)
