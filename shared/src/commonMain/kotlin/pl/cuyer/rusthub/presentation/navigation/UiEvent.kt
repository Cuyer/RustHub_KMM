package pl.cuyer.rusthub.presentation.navigation

import androidx.navigation3.runtime.NavKey

sealed interface UiEvent {
    data class Navigate(val destination: NavKey) : UiEvent
    data object NavigateUp: UiEvent
}