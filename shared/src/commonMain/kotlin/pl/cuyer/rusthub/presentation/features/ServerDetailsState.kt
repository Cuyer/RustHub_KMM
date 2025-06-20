package pl.cuyer.rusthub.presentation.features

import pl.cuyer.rusthub.presentation.model.ServerInfoUi

data class ServerDetailsState(
    val details: ServerInfoUi? = null,
    val isLoading: Boolean = false,
    val serverId: Long? = null,
    val serverName: String? = null
)
