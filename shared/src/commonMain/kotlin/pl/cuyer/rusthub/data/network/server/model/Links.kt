package pl.cuyer.domain.models.server.battlemetrics

import kotlinx.serialization.Serializable

@Serializable
data class Links(
    val prev: String?,
    val next: String?
)
