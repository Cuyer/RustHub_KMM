package pl.cuyer.rusthub.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class SearchQuery @OptIn(ExperimentalTime::class) constructor(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
