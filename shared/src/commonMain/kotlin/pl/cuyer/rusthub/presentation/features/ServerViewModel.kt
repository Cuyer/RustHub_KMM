package pl.cuyer.rusthub.presentation.features

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.map
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.data.local.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.domain.usecase.ClearFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersOptionsUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.SaveFiltersUseCase
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.model.toUi
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent

//TODO pomyśleć co zrobić żeby uniknąć importu z data do viewmodela (mapowanie)
class ServerViewModel(
    private val snackbarController: SnackbarController,
    getPagedServersUseCase: GetPagedServersUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val getFiltersOptions: GetFiltersOptionsUseCase,
    private val saveFiltersUseCase: SaveFiltersUseCase,
    private val clearFiltersUseCase: ClearFiltersUseCase
) : BaseViewModel() {

    @OptIn(ExperimentalCoroutinesApi::class)
    var paging: Flow<PagingData<ServerInfoUi>> = getPagedServersUseCase()
        .map {
            it.map { it.toServerInfo().toUi() }
        }.cachedIn(coroutineScope)


    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ServerState())
    val state = _state
        .onStart {
            observeFilters()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerState()
        )

    init {
        coroutineScope.launch {
            state.collectLatest {
                Napier.i("ServerState: $it")
            }
        }
    }

    private fun observeFilters() {
        combine(
            getFiltersOptions.invoke(),
            getFiltersUseCase.invoke()
        ) { filtersOptions, filters ->
            println("filtersOptions: $filtersOptions")
            println("filters: $filters")
            filters.toUi(
                maps = filtersOptions?.maps?.map { it.displayName } ?: emptyList(),
                flags = filtersOptions?.flags?.map { it.displayName } ?: emptyList(),
                regions = filtersOptions?.regions?.map { it.displayName } ?: emptyList(),
                difficulties = filtersOptions?.difficulty?.map { it.displayName } ?: emptyList(),
                schedules = filtersOptions?.wipeSchedules?.map { it.displayName } ?: emptyList(),
                playerCount = filtersOptions?.maxPlayerCount ?: 0,
                groupLimit = filtersOptions?.maxGroupLimit ?: 0,
                ranking = filtersOptions?.maxRanking ?: 0
            )
        }.filterNotNull()
            .onEach { mappedFilters ->
                _state.update {
                    it.copy(filters = mappedFilters)
                }
            }
            .flowOn(Dispatchers.Default)
            .launchIn(coroutineScope)
    }

    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> {}
            is ServerAction.OnChangeLoadingState -> updateLoading(action.isLoading)
            is ServerAction.OnRefresh -> {}
            is ServerAction.OnSaveFilters -> onSaveFilters(action.filters)
            is ServerAction.OnClearFilters -> clearFilters()
        }
    }

    private fun onSaveFilters(filters: ServerQuery) {
        saveFiltersUseCase(filters)
    }

    private fun clearFilters() {
        clearFiltersUseCase()
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