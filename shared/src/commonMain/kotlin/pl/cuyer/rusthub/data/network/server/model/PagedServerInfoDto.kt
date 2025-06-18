package pl.cuyer.rusthub.data.network.server.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedServerInfoDto(
    @SerialName("servers")
    val servers: List<ServerInfoDto>,
    @SerialName("size")
    val size: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_items")
    val totalItems: Int,
)
