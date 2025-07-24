package pl.cuyer.rusthub.presentation.user

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UserEvent {
    @Immutable
    data object LoggedOut : UserEvent
}
