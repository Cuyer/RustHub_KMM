package pl.cuyer.rusthub.presentation.features.server

import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import androidx.compose.runtime.Immutable

@Immutable
data class ServerDetailsState(
    val details: ServerInfoUi? = null,
    val isLoading: Boolean = true,
    val serverId: Long? = null,
    val serverName: String? = null,
    val showNotificationInfo: Boolean = false,
    val showMap: Boolean = false,
    val isConnected: Boolean = true
)
