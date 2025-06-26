package pl.cuyer.rusthub.presentation.features.server

import app.cash.paging.PagingData
import app.cash.paging.cachedIn
import app.cash.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock.System
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.data.local.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.domain.usecase.ClearFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersOptionsUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.SaveFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSearchQueryUseCase
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.SearchQueryUi
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.model.toUi
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.ClipboardHandler

//TODO pomyśleć co zrobić żeby uniknąć importu z data do viewmodela (mapowanie)
class ServerViewModel(
    private val clipboardHandler: ClipboardHandler,
    private val snackbarController: SnackbarController,
    getPagedServersUseCase: GetPagedServersUseCase,
    private val getFiltersUseCase: GetFiltersUseCase,
    private val getFiltersOptions: GetFiltersOptionsUseCase,
    private val saveFiltersUseCase: SaveFiltersUseCase,
    private val clearFiltersUseCase: ClearFiltersUseCase,
    private val saveSearchQueryUseCase: SaveSearchQueryUseCase,
    private val getSearchQueriesUseCase: GetSearchQueriesUseCase,
    private val deleteSearchQueriesUseCase: DeleteSearchQueriesUseCase,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")

    private val _state = MutableStateFlow(ServerState())
    val state = _state
        .onStart {
            observeFilters()
            observeSearchQueries()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerState()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val paging: Flow<PagingData<ServerInfoUi>> =
        queryFlow
            .flatMapLatest { query ->
            getPagedServersUseCase(
                searchQuery = query
            ).map { pagingData ->
                pagingData.map { it.toServerInfo().toUi() }
            }.flowOn(Dispatchers.Default)
        }.cachedIn(coroutineScope)
            .catch { e -> sendSnackbarEvent("Error occurred during fetching servers.") }

    private fun observeSearchQueries() {
        getSearchQueriesUseCase()
            .distinctUntilChanged()
            .map { searchQuery ->
                searchQuery.map { it.toUi() }
            }
            .flowOn(Dispatchers.Default)
            .onEach { mappedQuery ->
                updateSearchQuery(mappedQuery)
                updateIsLoadingSearchHistory(false)
            }
            .onStart { updateIsLoadingSearchHistory(true) }
            .catch { e -> sendSnackbarEvent("Error occurred during fetching search history.") }
            .launchIn(coroutineScope)
    }

    private fun updateSearchQuery(mappedQuery: List<SearchQueryUi>) {
        _state.update {
            it.copy(
                searchQuery = mappedQuery
            )
        }
    }


    private fun observeFilters() {
        combine(
            getFiltersOptions.invoke(),
            getFiltersUseCase.invoke()
        ) { filtersOptions, filters ->
            updateFilter(filters?.filter ?: ServerFilter.ALL)
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
        }.distinctUntilChanged()
            .flowOn(Dispatchers.Default)
            .onStart { updateIsLoadingFilters(true) }
            .onEach { mappedFilters ->
                updateFilters(mappedFilters)
                updateIsLoadingFilters(false)
            }
            .catch { e -> sendSnackbarEvent("Error occurred during fetching filters.") }
            .launchIn(coroutineScope)
    }

    private fun updateFilters(mappedFilters: FilterUi) {
        _state.update {
            it.copy(
                filters = mappedFilters
            )
        }
    }

    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> navigateToServer(action.id, action.name)
            is ServerAction.OnLongServerClick -> saveIpToClipboard(action.ipAddress)
            is ServerAction.OnChangeIsRefreshingState -> updateIsRefreshing(action.isRefreshing)
            is ServerAction.OnSaveFilters -> onSaveFilters(action.filters)
            is ServerAction.OnSearch -> handleSearch(query = action.query)
            is ServerAction.OnClearFilters -> clearFilters()
            is ServerAction.OnClearSearchQuery -> clearSearchQuery()
            is ServerAction.DeleteSearchQueries -> deleteSearchQueries(null)
            is ServerAction.DeleteSearchQueryByQuery -> deleteSearchQueries(action.query)
            is ServerAction.OnError -> sendSnackbarEvent(action.message)
            is ServerAction.OnChangeLoadMoreState -> updateLoadingMore(action.isLoadingMore)
            is ServerAction.OnFilterChange -> coroutineScope.launch {
                updateFilter(action.filter)
            }
        }
    }

    private fun deleteSearchQueries(query: String?) {
        coroutineScope.launch {
            runCatching {
                query?.let {
                    deleteSearchQueriesUseCase(query)
                } ?: run {
                    deleteSearchQueriesUseCase()
                }
            }.onFailure { e ->
                query?.let {
                    sendSnackbarEvent("Error occurred during deleting search query")
                } ?: run {
                    sendSnackbarEvent("Error occurred during deleting search queries")
                }
            }
        }
    }

    private fun handleSearch(query: String) {
        coroutineScope.launch {
            runCatching {
                saveSearchQueryUseCase(
                    SearchQuery(
                        query = query,
                        timestamp = System.now(),
                        id = null
                    )
                )
            }.onFailure {
                sendSnackbarEvent("Error occurred during saving searched phrase.")
            }.onSuccess {
                queryFlow.update { query }
            }
        }
    }

    private fun clearSearchQuery() {
        queryFlow.update { "" }
    }

    private fun saveIpToClipboard(ipAddress: String?) {
        ipAddress?.let {
            clipboardHandler.copyToClipboard("Server address", it)
            coroutineScope.launch {
                snackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "Saved $it to the clipboard!",
                        duration = Duration.SHORT
                    )
                )
            }
        } ?: run {
            coroutineScope.launch {
                snackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = "There is no IP available for this server.",
                        duration = Duration.SHORT
                    )
                )
            }
        }

    }

    private fun onSaveFilters(filters: ServerQuery) {
        coroutineScope.launch {
            runCatching {
                saveFiltersUseCase(filters)
            }.onFailure {
                sendSnackbarEvent("Error occurred during saving filters.")
            }
        }
    }

    private fun clearFilters() {
        coroutineScope.launch {
            runCatching {
                clearFiltersUseCase()
            }.onFailure {
                sendSnackbarEvent("Error occurred during clearing filters.")
            }
        }
    }

    private fun navigateToServer(id: Long, name: String) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ServerDetails(id, name)))
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

    private fun updateIsLoadingFilters(loading: Boolean) {
        _state.update {
            it.copy(
                isLoadingFilters = loading
            )
        }
    }

    private fun updateIsLoadingSearchHistory(loading: Boolean) {
        _state.update {
            it.copy(
                isLoadingSearchHistory = loading
            )
        }
    }

    private fun updateIsRefreshing(isRefreshing: Boolean) {
        _state.update { it.copy(isRefreshing = isRefreshing) }
    }

    private fun updateLoadingMore(loading: Boolean) {
        _state.update { it.copy(loadingMore = loading) }
    }

    private suspend fun updateFilter(filter: ServerFilter) {
        withContext(Dispatchers.Main.immediate) {
            runCatching {
                val current = getFiltersUseCase().first() ?: ServerQuery()
                saveFiltersUseCase(current.copy(filter = filter))
            }.onFailure {
                sendSnackbarEvent("Error occurred during saving filters.")
            }
        }
    }
}