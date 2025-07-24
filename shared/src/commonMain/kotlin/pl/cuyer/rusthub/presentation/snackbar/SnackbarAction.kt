package pl.cuyer.rusthub.presentation.snackbar

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.receiveAsFlow

@Immutable
data class SnackbarEvent(
    val message: String,
    val action: SnackbarAction? = null,
    val duration: Duration = Duration.SHORT
)

@Immutable
data class SnackbarAction(
    val name: String,
    val action: () -> Unit
)

@Immutable
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