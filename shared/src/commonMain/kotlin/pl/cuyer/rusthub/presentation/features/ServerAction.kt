package pl.cuyer.rusthub.presentation.features

import pl.cuyer.rusthub.domain.model.ServerQuery

sealed interface ServerAction {
    data class OnServerClick(val mapId: String?, val serverId: Long): ServerAction
    data class OnChangeLoadingState(val isLoading: Boolean): ServerAction
    data class OnSaveFilters(val filters: ServerQuery): ServerAction
    data class OnLongServerClick(val ipAddress: String?) : ServerAction
    data object OnClearFilters: ServerAction
}