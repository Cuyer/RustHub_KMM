package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.datetime.LocalDateTime

@Immutable
data class Raid(
    val id: String,
    val name: String,
    val dateTime: LocalDateTime,
    val target: String,
    val description: String?
)
