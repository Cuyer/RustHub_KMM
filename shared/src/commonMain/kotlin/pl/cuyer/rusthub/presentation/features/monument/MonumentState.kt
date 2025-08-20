package pl.cuyer.rusthub.presentation.features.monument

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.model.MonumentSyncState
import pl.cuyer.rusthub.presentation.model.SearchQueryUi

@Immutable
data class MonumentState(
    val isRefreshing: Boolean = true,
    val searchQuery: List<SearchQueryUi> = emptyList(),
    val isLoadingSearchHistory: Boolean = true,
    val selectedType: MonumentType? = null,
    val syncState: MonumentSyncState = MonumentSyncState.DONE,
)
