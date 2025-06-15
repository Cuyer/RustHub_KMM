package pl.cuyer.rusthub.presentation.features

import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import io.github.aakira.napier.LogLevel
import io.github.aakira.napier.Napier
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.PrepareRustMapUseCase

class ServerViewModel(
    getPagedServersUseCase: GetPagedServersUseCase,
    private val prepareRustMapUseCase: PrepareRustMapUseCase
) : BaseViewModel() {

    var paging: Flow<PagingData<ServerInfo>>? = getPagedServersUseCase(ServerQuery())
        .cachedIn(coroutineScope)

    private val _state = MutableStateFlow(ServerState())
    val state = _state
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerState()
        )


    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> prepareRustMap(action.mapId, action.serverId)
        }
    }


    private fun prepareRustMap(mapId: String?, serverId: Long) {
        mapId?.let {
            coroutineScope.launch {
                prepareRustMapUseCase(mapId, serverId).collectLatest { result ->
                    when (result) {
                        is Result.Success -> navigateToServer()
                        is Result.Loading -> updateLoading(true)
                        is Result.Error -> handleError(result.exception)
                    }
                }
            }
        }
    }

    private fun navigateToServer() {

    }

    private fun handleError(e: Throwable) {

    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}