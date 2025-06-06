package pl.cuyer.rusthub.presentation.snackbar

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.receiveAsFlow

data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
    val duration: Duration = Duration.SHORT
)

data class SnackbarAction(
    val name: String,
    val action: () -> Unit
)

enum class Duration {
    SHORT,
    LONG,
    INDEFINITE
}

object SnackbarController {
    private val _events = Channel<SnackbarEvent>(UNLIMITED)
    val events = _events.receiveAsFlow()

    suspend fun sendEvent(event: SnackbarEvent) {
        _events.send(event)
    }
}