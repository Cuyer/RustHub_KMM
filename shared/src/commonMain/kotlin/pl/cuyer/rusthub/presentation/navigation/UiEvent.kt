package pl.cuyer.rusthub.presentation.navigation

sealed interface UiEvent {
    data class Navigate(val destination: Destination): UiEvent
    data object NavigateUp: UiEvent
}