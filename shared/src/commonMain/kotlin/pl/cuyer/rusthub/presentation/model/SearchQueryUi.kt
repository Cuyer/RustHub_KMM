package pl.cuyer.rusthub.presentation.model

import kotlinx.datetime.Instant
import androidx.compose.runtime.Immutable

@Immutable
data class SearchQueryUi(
    val id: Long?,
    val query: String,
    val timestamp: Instant
)
