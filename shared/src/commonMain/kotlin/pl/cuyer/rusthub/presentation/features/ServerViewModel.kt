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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.model.toUi
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

class ServerViewModel(
    private val snackbarController: SnackbarController,
    getPagedServersUseCase: GetPagedServersUseCase
) : BaseViewModel() {

    private val queryFlow = MutableStateFlow(ServerQuery())

    @OptIn(ExperimentalCoroutinesApi::class)
    var paging: Flow<PagingData<ServerInfoUi>> = queryFlow
        .flatMapLatest { query ->
            getPagedServersUseCase(query).map { it.map { it.toServerInfo().toUi() } }
        }
        .cachedIn(coroutineScope)

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ServerState())
    val state = _state
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerState()
        )

    var fetchAllServersJob: Job? = null

    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> {}
            is ServerAction.OnChangeLoadingState -> updateLoading(action.isLoading)
            is ServerAction.OnRefresh -> {

            }

            is ServerAction.OnStopAllJobs -> {}
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