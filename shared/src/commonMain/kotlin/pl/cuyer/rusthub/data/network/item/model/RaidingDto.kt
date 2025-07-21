package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaidingDto(
    val startingItem: StartingItemDto? = null,
    val timeToRaid: Int? = null,
    val amount: List<RaidItemDto>? = null,
    val rawMaterialCost: List<RaidResourceDto>? = null,
)
