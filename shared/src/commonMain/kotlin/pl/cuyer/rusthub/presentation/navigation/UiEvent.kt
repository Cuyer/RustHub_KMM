package pl.cuyer.rusthub.presentation.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey

@Immutable
sealed interface UiEvent {
    @Immutable
    data class Navigate(val destination: NavKey) : UiEvent
    @Immutable
    data object NavigateUp: UiEvent
}