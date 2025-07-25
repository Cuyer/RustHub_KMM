package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class ItemsResponseDto(
    val items: List<RustItemDto>
)
