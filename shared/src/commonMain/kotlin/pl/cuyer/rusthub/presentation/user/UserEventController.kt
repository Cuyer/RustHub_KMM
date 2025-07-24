package pl.cuyer.rusthub.presentation.user

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.receiveAsFlow

object UserEventController {
    private val _events = Channel<UserEvent>(UNLIMITED)
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: UserEvent) {
        _events.send(event)
    }
}
