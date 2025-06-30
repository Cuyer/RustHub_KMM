package pl.cuyer.rusthub.presentation.features.auth.delete

data class DeleteAccountState(
    val username: String = "",
    val password: String = "",
    val usernameError: String? = null,
    val passwordError: String? = null,
    val isLoading: Boolean = false
)
