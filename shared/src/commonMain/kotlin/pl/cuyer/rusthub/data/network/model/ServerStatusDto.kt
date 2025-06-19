package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.Serializable

@Serializable
enum class ServerStatusDto {
    ONLINE, OFFLINE
}