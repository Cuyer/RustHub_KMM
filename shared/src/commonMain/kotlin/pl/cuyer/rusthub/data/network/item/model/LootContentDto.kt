package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class LootContentDto(
    val spawn: String? = null,
    val image: String? = null,
    val stack: LootAmountDto? = null,
    val chance: Double? = null,
    val amount: LootAmountDto? = null,
)
