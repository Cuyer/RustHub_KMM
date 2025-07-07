package pl.cuyer.rusthub.presentation.features.auth.delete

sealed interface DeleteAccountAction {
    data object OnDelete : DeleteAccountAction
    data class OnPasswordChange(val password: String) : DeleteAccountAction
}
