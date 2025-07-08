package pl.cuyer.rusthub.presentation.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

data class SearchQueryUi @OptIn(ExperimentalTime::class) constructor(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
