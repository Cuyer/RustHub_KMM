package pl.cuyer.rusthub.presentation.features.server

sealed interface ServerDetailsAction {
    data class OnSaveToClipboard(val ipAddress: String) : ServerDetailsAction
    data object ToggleFavourite : ServerDetailsAction
}