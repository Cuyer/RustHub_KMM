package pl.cuyer.rusthub.presentation.features.auth.delete

data class DeleteAccountState(
    val password: String = "",
    val passwordError: String? = null,
    val isLoading: Boolean = false
)
