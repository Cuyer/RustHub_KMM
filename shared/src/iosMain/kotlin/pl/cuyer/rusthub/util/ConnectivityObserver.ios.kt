package pl.cuyer.rusthub.util

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

actual class ConnectivityObserver {
    actual val isConnected: Flow<Boolean> = flowOf(true)
}
