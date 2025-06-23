package pl.cuyer.rusthub.presentation.features.server

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.usecase.GetServerDetailsUseCase
import pl.cuyer.rusthub.domain.usecase.ToggleFavouriteUseCase
import pl.cuyer.rusthub.presentation.model.toUi
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.ClipboardHandler

class ServerDetailsViewModel(
    private val clipboardHandler: ClipboardHandler,
    private val snackbarController: SnackbarController,
    private val getServerDetailsUseCase: GetServerDetailsUseCase,
    private val toggleFavouriteUseCase: ToggleFavouriteUseCase,
    private val serverName: String?,
    private val serverId: Long?
) : BaseViewModel() {
    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ServerDetailsState())
    val state = _state
        .onStart {
            _state.update {
                it.copy(
                    isLoading = true,
                    serverId = serverId,
                    serverName = serverName,
                    details = it.details
                )
            }
            serverId?.let {
                observeServerDetails(it)
            }
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerDetailsState()
        )

    init {
        coroutineScope.launch {
            state.collectLatest {
                Napier.d("State: $it")
            }
        }
    }

    private var toggleJob: Job? = null

    fun onAction(action: ServerDetailsAction) {
        when (action) {
            is ServerDetailsAction.OnSaveToClipboard -> saveIpToClipboard(action.ipAddress)
            ServerDetailsAction.OnToggleFavourite -> toggleFavourite()
        }
    }

    private fun saveIpToClipboard(ipAddress: String) {
        clipboardHandler.copyToClipboard("Server address", ipAddress)
        coroutineScope.launch {
            snackbarController.sendEvent(
                event = SnackbarEvent(
                    message = "Saved $ipAddress to the clipboard!",
                    duration = Duration.SHORT
                )
            )
        }
    }


    private fun toggleFavourite() {
        val id = state.value.serverId ?: return
        val add = state.value.details?.isFavorite != true

        toggleJob?.cancel()

        toggleJob = coroutineScope.launch {
            toggleFavouriteUseCase(id, add)
                .onStart {
                    _state.update {
                        it.copy(
                            isSyncing = true
                        )
                    }
                }
                .catch { e -> showErrorSnackbar(e.message ?: "Unknown error") }
                .onCompletion {
                    _state.update {
                        it.copy(
                            isSyncing = false
                        )
                    }
                }
                .collectLatest { result ->
                when (result) {
                    is Result.Success -> _state.update {
                        it.copy(
                            isSyncing = false,
                            details = it.details?.copy(isFavorite = !add)
                        )
                    }
                    is Result.Error -> {
                        showErrorSnackbar(result.exception.message ?: "Unknown error")
                    }
                    Result.Loading -> {}
                }
            }
        }
    }

    private suspend fun showErrorSnackbar(message: String) {
        snackbarController.sendEvent(
            SnackbarEvent(message = message, action = null)
        )
    }


    private fun observeServerDetails(serverId: Long) {
        getServerDetailsUseCase.invoke(serverId)
            .map { it?.toUi() }
            .onEach { mappedDetails ->
                _state.update {
                    it.copy(
                        details = mappedDetails,
                        isLoading = false
                    )
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(coroutineScope)
    }
}