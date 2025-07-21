package pl.cuyer.rusthub.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.receiveAsFlow

object RootNavigator {
    private val _events = Channel<NavKey>(UNLIMITED)
    val events = _events.receiveAsFlow()

    suspend fun navigate(destination: NavKey) {
        _events.send(destination)
    }
}
