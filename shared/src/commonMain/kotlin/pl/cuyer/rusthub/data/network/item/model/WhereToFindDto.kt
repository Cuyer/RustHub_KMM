package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class WhereToFindDto(
    val place: String? = null,
    val image: String? = null,
    val amount: LootAmountDto? = null,
)
