package pl.cuyer.rusthub.presentation.features.auth.confirm

sealed interface ConfirmEmailAction {
    data object OnConfirm : ConfirmEmailAction
    data object OnResend : ConfirmEmailAction
}
