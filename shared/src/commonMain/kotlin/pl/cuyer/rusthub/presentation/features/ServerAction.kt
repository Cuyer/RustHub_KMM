package pl.cuyer.rusthub.presentation.features

sealed interface ServerAction {
    data class OnServerClick(val mapId: String?, val serverId: Long): ServerAction
    data class OnChangeLoadingState(val isLoading: Boolean): ServerAction
    data object OnRefresh: ServerAction
    data object OnStopAllJobs: ServerAction
}