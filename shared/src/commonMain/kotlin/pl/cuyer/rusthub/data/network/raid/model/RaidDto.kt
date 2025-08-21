package pl.cuyer.rusthub.data.network.raid.model

import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RaidDto(
    val id: String,
    val name: String,
    @SerialName("dateTime") val dateTime: Instant,
    val steamIds: List<String>,
    val description: String? = null
)

