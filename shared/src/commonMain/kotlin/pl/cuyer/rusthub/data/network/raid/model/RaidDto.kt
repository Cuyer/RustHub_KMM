package pl.cuyer.rusthub.data.network.raid.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaidDto(
    val id: String,
    val name: String,
    @SerialName("dateTime") val dateTime: String,
    val steamIds: List<String>,
    val description: String? = null
)

