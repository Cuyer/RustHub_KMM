package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

@Serializable
data class ItemsResponseDto(
    val page: Int,
    val size: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_items")
    val totalItems: Long,
    val items: List<ItemSummaryDto>
)
