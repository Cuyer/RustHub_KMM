package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class LootingDto(
    val from: String? = null,
    val image: String? = null,
    val chance: Double? = null,
    val amount: LootAmountDto? = null,
)
