package pl.cuyer.rusthub.presentation.features.auth.delete

import androidx.compose.runtime.Immutable

@Immutable
sealed interface DeleteAccountAction {
    @Immutable
    data object OnDelete : DeleteAccountAction
    @Immutable
    data object OnConfirmDelete : DeleteAccountAction
    @Immutable
    data object OnDismissDialog : DeleteAccountAction
    @Immutable
    data class OnPasswordChange(val password: String) : DeleteAccountAction
}
