package pl.cuyer.rusthub.domain.model

import kotlin.time.Instant
import androidx.compose.runtime.Immutable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Immutable
data class SearchQuery(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
