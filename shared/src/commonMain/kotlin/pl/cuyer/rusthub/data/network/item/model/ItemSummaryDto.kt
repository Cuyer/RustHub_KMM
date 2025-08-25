package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemSummaryDto(
    val id: Long,
    val name: String? = null,
    @SerialName("short_name")
    val shortName: String? = null,
    val image: String? = null,
    val categories: List<ItemCategoryDto>? = null,
    val description: String? = null
)
