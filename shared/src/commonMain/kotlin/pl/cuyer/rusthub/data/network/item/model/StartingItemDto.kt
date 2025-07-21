package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class StartingItemDto(
    val icon: String? = null,
    val name: String? = null,
)
