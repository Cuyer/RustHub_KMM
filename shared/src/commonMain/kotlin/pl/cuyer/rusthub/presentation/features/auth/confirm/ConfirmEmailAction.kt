package pl.cuyer.rusthub.presentation.features.auth.confirm

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ConfirmEmailAction {
    @Immutable
    data object OnConfirm : ConfirmEmailAction
    @Immutable
    data object OnResend : ConfirmEmailAction
    @Immutable
    data object OnBack : ConfirmEmailAction
}
