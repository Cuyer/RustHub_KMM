package pl.cuyer.rusthub.presentation.features.server

import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.SearchQueryUi
import pl.cuyer.rusthub.domain.model.ServerFilter
import androidx.compose.runtime.Immutable

@Immutable
data class ServerState(
    val isLoading: Boolean = true,
    val isRefreshing: Boolean = false,
    val filters: FilterUi? = null,
    val searchQuery: List<SearchQueryUi> = emptyList(),
    val isLoadingSearchHistory: Boolean = true,
    val isLoadingFilters: Boolean = true,
    val loadingMore: Boolean = false,
    val filter: ServerFilter = ServerFilter.ALL,
    val isConnected: Boolean = true
)
