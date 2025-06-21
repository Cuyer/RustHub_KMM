package pl.cuyer.rusthub.presentation.features

sealed interface ServerDetailsAction {
    data class OnSaveToClipboard(val ipAddress: String) : ServerDetailsAction
}