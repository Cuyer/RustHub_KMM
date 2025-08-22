package pl.cuyer.rusthub.data.network.raid.model

import kotlin.time.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
data class RaidDto(
    val id: String,
    val name: String,
    @SerialName("dateTime") val dateTime: Instant,
    val steamIds: List<String>,
    val description: String? = null
)

