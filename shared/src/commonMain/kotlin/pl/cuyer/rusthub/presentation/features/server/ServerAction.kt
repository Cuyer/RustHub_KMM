package pl.cuyer.rusthub.presentation.features.server

import pl.cuyer.rusthub.domain.model.ServerQuery

sealed interface ServerAction {
    data class OnServerClick(val id: Long, val name: String) : ServerAction
    data class OnChangeLoadingState(val isLoading: Boolean): ServerAction
    data class OnSaveFilters(val filters: ServerQuery): ServerAction
    data class OnLongServerClick(val ipAddress: String?) : ServerAction
    data class OnSearch(val query: String) : ServerAction
    data object OnClearFilters: ServerAction
    data object DeleteSearchQueries : ServerAction
    data class DeleteSearchQueryByQuery(val query: String) : ServerAction
    data object OnClearSearchQuery : ServerAction
}