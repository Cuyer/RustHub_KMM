package pl.cuyer.rusthub.common.user

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UserEvent {
    @Immutable
    data object LoggedOut : UserEvent
}
