package pl.cuyer.rusthub.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

actual class ConnectivityObserver(private val context: Context) {

    actual val isConnected: Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        fun isOnlineNow(): Boolean {
            val network = cm.activeNetwork ?: return false
            val caps = cm.getNetworkCapabilities(network) ?: return false
            val hasInternet = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
            val isWifiCellEth = caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)
            return hasInternet && isWifiCellEth
        }

        val callback = object : NetworkCallback() {
            override fun onCapabilitiesChanged(n: Network, caps: NetworkCapabilities) {
                val ok = caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                        caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED) &&
                        (caps.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                caps.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                                caps.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
                trySend(ok)
            }

            override fun onAvailable(network: Network) {
                trySend(isOnlineNow())
            }

            override fun onLost(network: Network) {
                trySend(false)
            }

            override fun onUnavailable() {
                trySend(false)
            }
        }

        cm.registerDefaultNetworkCallback(callback)
        trySend(isOnlineNow())

        awaitClose { cm.unregisterNetworkCallback(callback) }
    }.distinctUntilChanged()
}
