package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PagedServerInfo(
    @SerialName("servers")
    val servers: List<ServerInfo>,
    @SerialName("size")
    val size: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_items")
    val totalItems: Int,
)
