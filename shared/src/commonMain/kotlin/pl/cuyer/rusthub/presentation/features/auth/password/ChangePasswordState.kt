package pl.cuyer.rusthub.presentation.features.auth.password

data class ChangePasswordState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val oldPasswordError: String? = null,
    val newPasswordError: String? = null,
    val isLoading: Boolean = false
)
