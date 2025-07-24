package pl.cuyer.rusthub.presentation.features.server

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.ServerFilter

@Immutable
sealed interface ServerAction {
    @Immutable
    data class OnServerClick(val id: Long, val name: String) : ServerAction
    @Immutable
    data class OnChangeIsRefreshingState(val isRefreshing: Boolean): ServerAction
    @Immutable
    data class OnSaveFilters(val filters: ServerQuery): ServerAction
    @Immutable
    data class OnLongServerClick(val ipAddress: String?) : ServerAction
    @Immutable
    data class OnSearch(val query: String) : ServerAction
    @Immutable
    data object OnClearFilters: ServerAction
    @Immutable
    data object DeleteSearchQueries : ServerAction
    @Immutable
    data class DeleteSearchQueryByQuery(val query: String) : ServerAction
    @Immutable
    data object OnClearSearchQuery : ServerAction
    @Immutable
    data class OnError(val exception: Throwable): ServerAction
    @Immutable
    data class OnChangeLoadMoreState(val isLoadingMore: Boolean): ServerAction
    @Immutable
    data class OnFilterChange(val filter: ServerFilter): ServerAction
}