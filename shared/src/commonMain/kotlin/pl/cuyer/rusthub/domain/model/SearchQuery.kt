package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.Instant

data class SearchQuery(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
