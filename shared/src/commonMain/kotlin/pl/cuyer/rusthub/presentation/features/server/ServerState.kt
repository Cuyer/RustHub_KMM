package pl.cuyer.rusthub.presentation.features.server

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.SearchQueryUi

@Immutable
data class ServerState(
    val filters: FilterUi? = null,
    val searchQuery: List<SearchQueryUi> = emptyList(),
    val isLoadingSearchHistory: Boolean = true,
    val isLoadingFilters: Boolean = true,
    val query: String = "",
    val isConnected: Boolean = true
)
