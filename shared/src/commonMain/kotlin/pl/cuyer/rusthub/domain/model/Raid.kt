package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Raid(
    val id: String,
    val name: String,
    val dateTime: LocalDateTime,
    val steamIds: List<String>,
    val description: String?
)
