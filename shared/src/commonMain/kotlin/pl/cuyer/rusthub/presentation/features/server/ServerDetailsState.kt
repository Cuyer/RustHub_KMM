package pl.cuyer.rusthub.presentation.features.server

import pl.cuyer.rusthub.presentation.model.ServerInfoUi

data class ServerDetailsState(
    val details: ServerInfoUi? = null,
    val isLoading: Boolean = true,
    val serverId: Long? = null,
    val serverName: String? = null,
    val showSubscriptionDialog: Boolean = false
)
