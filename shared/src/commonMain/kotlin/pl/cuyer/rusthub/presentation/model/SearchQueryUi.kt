package pl.cuyer.rusthub.presentation.model

import kotlinx.datetime.Instant

data class SearchQueryUi(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
