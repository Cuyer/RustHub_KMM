package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.Instant
import androidx.compose.runtime.Immutable

@Immutable
data class SearchQuery(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
