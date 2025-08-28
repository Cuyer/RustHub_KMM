package pl.cuyer.rusthub.presentation.navigation

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UiEvent {
    @Immutable
    data class Navigate(val destination: NavKey) : UiEvent
    @Immutable
    data object NavigateUp: UiEvent
    @Immutable
    data class ScrollToIndex(val index: Int) : UiEvent
    @Immutable
    data object RefreshList : UiEvent
}