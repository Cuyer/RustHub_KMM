package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class LootAmountDto(
    val min: Int? = null,
    val max: Int? = null,
)
