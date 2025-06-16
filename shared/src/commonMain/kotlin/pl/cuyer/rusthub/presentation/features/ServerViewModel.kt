package pl.cuyer.rusthub.presentation.features

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.map
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.onEach
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.usecase.FetchAllServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.PrepareRustMapUseCase
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

class ServerViewModel(
    private val snackbarController: SnackbarController,
    getPagedServersUseCase: GetPagedServersUseCase,
    private val prepareRustMapUseCase: PrepareRustMapUseCase,
    private val fetchAllServersUseCase: FetchAllServersUseCase
) : BaseViewModel() {

    private val queryFlow = MutableStateFlow(ServerQuery())

    @OptIn(ExperimentalCoroutinesApi::class)
    var paging: Flow<PagingData<ServerInfo>> = queryFlow
        .flatMapLatest { query ->
            getPagedServersUseCase(query).map { it.map { it.toServerInfo() } }
        }
        .cachedIn(coroutineScope)

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ServerState())
    val state = _state
        .onStart {
            observeFetchingServers()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerState()
        )

    var fetchAllServersJob: Job? = null

    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> prepareRustMap(action.mapId, action.serverId)
            is ServerAction.OnChangeLoadingState -> updateLoading(action.isLoading)
            is ServerAction.OnRefresh -> observeFetchingServers()
            is ServerAction.OnStopAllJobs -> stopAllJobs()
        }
    }

    private fun stopAllJobs() {
        fetchAllServersJob?.cancel()
        fetchAllServersJob = null
    }
    private fun observeFetchingServers() {
        fetchAllServersJob?.cancel()
        fetchAllServersJob = fetchAllServersUseCase()
            .onEach {
                when (it) {
                    is Result.Success -> updateLoading(false)
                    is Result.Loading -> updateLoading(true)
                    is Result.Error -> handleError(it.exception)
                }
            }
            .launchIn(coroutineScope)
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
        updateLoading(false)

    }

    private fun handleError(e: Throwable) {
        updateLoading(false)
        e.message?.let {
            sendSnackbarEvent(
                message = it
            )
        }
    }

    private fun sendSnackbarEvent(
        message: String,
        actionText: String? = null,
        action: () -> Unit = {},
        duration: Duration? = null
    ) {
        coroutineScope.launch {
            snackbarController.sendEvent(
                event = SnackbarEvent(
                    message = message,
                    action = actionText?.let { SnackbarAction(name = it, action = action) },
                    duration = duration ?: Duration.SHORT
                )
            )
        }
    }

    private fun updateLoading(isLoading: Boolean) {
        _state.update {
            it.copy(
                isLoading = isLoading
            )
        }
    }
}