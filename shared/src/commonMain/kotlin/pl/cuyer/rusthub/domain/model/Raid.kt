package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.LocalDateTime

data class Raid(
    val id: String,
    val name: String,
    val dateTime: LocalDateTime,
    val target: String,
    val description: String?
)
