package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaidResourceDto(
    val icon: String? = null,
    val name: String? = null,
    val amount: Int? = null,
    @SerialName("mixingTableAmount") val mixingTableAmount: Int? = null,
)
