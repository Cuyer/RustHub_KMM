package pl.cuyer.rusthub.util

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import platform.Network.NWPathMonitor
import platform.Network.nw_path_monitor_cancel
import platform.Network.nw_path_monitor_create
import platform.Network.nw_path_monitor_set_queue
import platform.Network.nw_path_monitor_set_update_handler
import platform.Network.nw_path_get_status
import platform.Network.nw_path_monitor_start
import platform.Network.nw_path_status_t
import platform.Network.nw_path_status_satisfied
import platform.darwin.dispatch_get_main_queue

actual class ConnectivityObserver {
    private val monitor: NWPathMonitor = nw_path_monitor_create()

    actual val isConnected: Flow<Boolean> = callbackFlow {
        nw_path_monitor_set_update_handler(monitor) { path ->
            val status: nw_path_status_t = nw_path_get_status(path)
            trySend(status == nw_path_status_satisfied).isSuccess
        }
        nw_path_monitor_set_queue(monitor, dispatch_get_main_queue())
        nw_path_monitor_start(monitor)
        awaitClose {
            nw_path_monitor_cancel(monitor)
        }
    }.distinctUntilChanged().conflate()
}
