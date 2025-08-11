package pl.cuyer.rusthub.presentation.features.server

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
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
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.domain.usecase.ClearFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersOptionsUseCase
import pl.cuyer.rusthub.domain.usecase.GetFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.GetPagedServersUseCase
import pl.cuyer.rusthub.domain.usecase.GetSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.SaveFiltersUseCase
import pl.cuyer.rusthub.domain.usecase.SaveSearchQueryUseCase
import pl.cuyer.rusthub.domain.usecase.ClearServerCacheUseCase
import pl.cuyer.rusthub.presentation.model.FilterUi
import pl.cuyer.rusthub.presentation.model.SearchQueryUi
import pl.cuyer.rusthub.presentation.model.ServerInfoUi
import pl.cuyer.rusthub.presentation.model.toDomain
import pl.cuyer.rusthub.presentation.model.toUi
import pl.cuyer.rusthub.domain.model.toUiModel
import pl.cuyer.rusthub.presentation.navigation.ServerDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.ClipboardHandler
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.ConnectivityObserver
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import kotlin.time.Clock.System
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.util.AdsConsentManager
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
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
    private val clearServerCacheUseCase: ClearServerCacheUseCase,
    private val stringProvider: StringProvider,
    private val connectivityObserver: ConnectivityObserver,
    private val getUserUseCase: GetUserUseCase,
    private val adsConsentManager: AdsConsentManager,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")

    private val _state = MutableStateFlow(ServerState())
    val state = _state
        .onStart {
            observeFilters()
            observeSearchQueries()
            observeConnectivity()
        }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ServerState()
        )

    private val filterChangeFlow = _state
        .map { it.filter }
        .distinctUntilChanged()

    val showAds = getUserUseCase()
        .map { user -> !(user?.subscribed ?: false) && adsConsentManager.canRequestAds }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = true
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val paging: Flow<PagingData<ServerInfoUi>> =
        combine(queryFlow, filterChangeFlow) { query, _ -> query }
            .flatMapLatest { query ->
                getPagedServersUseCase(
                    searchQuery = query
                ).map { pagingData ->
                    pagingData.map { it.toUiModel(stringProvider) }
                }.flowOn(Dispatchers.Default)
            }
            .cachedIn(coroutineScope)
            .catchAndLog {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_fetching_servers))
            }

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
            .catchAndLog {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_fetching_search_history))
            }
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
            filters.toUi(
                stringProvider = stringProvider,
                maps = filtersOptions?.maps?.map { it.displayName } ?: emptyList(),
                flags = filtersOptions?.flags?.map { it.displayName } ?: emptyList(),
                regions = filtersOptions?.regions?.map { it.displayName(stringProvider) }
                    ?: emptyList(),
                difficulties = filtersOptions?.difficulty?.map { it.displayName } ?: emptyList(),
                schedules = filtersOptions?.wipeSchedules?.map { it.displayName(stringProvider) }
                    ?: emptyList(),
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
            .catchAndLog {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_fetching_filters))
            }
            .launchIn(coroutineScope)
    }

    private fun updateFilters(mappedFilters: FilterUi) {
        _state.update {
            it.copy(
                filters = mappedFilters,
                filter = mappedFilters.filter
            )
        }
    }

    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> navigateToServer(action.id, action.name)
            is ServerAction.OnLongServerClick -> saveIpToClipboard(action.ipAddress)
            is ServerAction.OnSaveFilters -> onSaveFilters(action.filters)
            is ServerAction.OnSearch -> handleSearch(query = action.query)
            is ServerAction.OnClearFilters -> clearFilters()
            is ServerAction.OnClearSearchQuery -> clearSearchQuery()
            is ServerAction.DeleteSearchQueries -> deleteSearchQueries(null)
            is ServerAction.DeleteSearchQueryByQuery -> deleteSearchQueries(action.query)
            is ServerAction.OnError -> sendSnackbarEvent(
                action.exception.toUserMessage(stringProvider) ?: stringProvider.get(
                    SharedRes.strings.error_unknown
                )
            )

            is ServerAction.OnChangeLoadMoreState -> updateLoadingMore(action.isLoadingMore)
            is ServerAction.OnFilterChange -> updateFilter(action.filter)
            is ServerAction.GatherConsent -> gatherConsent(action.activity, action.onAdAvailable)
        }
    }

    private fun deleteSearchQueries(query: String?) {
        coroutineScope.launch {
            runCatching {
                if (query != null) deleteSearchQueriesUseCase(query)
                else deleteSearchQueriesUseCase()
            }.onFailure {
                CrashReporter.recordException(it)
                val msg = if (query != null) {
                    stringProvider.get(SharedRes.strings.error_deleting_query)
                } else {
                    stringProvider.get(SharedRes.strings.error_deleting_queries)
                }
                sendSnackbarEvent(msg)
            }
        }
    }

    private fun handleSearch(query: String) {
        if (query.isNotEmpty()) {
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
                    CrashReporter.recordException(it)
                    sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_saving_search))
                }.onSuccess {
                    queryFlow.update { query }
                    clearServerCache()
                }
            }
        } else {
            sendSnackbarEvent(stringProvider.get(SharedRes.strings.server_query_cannot_be_empty))
        }
    }

    private fun clearSearchQuery() {
        coroutineScope.launch {
            if (queryFlow.value.isNotEmpty()) {
                queryFlow.update { "" }
                clearServerCache()
            }
        }
    }

    private fun saveIpToClipboard(ipAddress: String?) {
        ipAddress?.let {
            clipboardHandler.copyToClipboard(
                stringProvider.get(SharedRes.strings.server_address),
                "client.connect $it"
            )
            coroutineScope.launch {
                snackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = stringProvider.get(
                            SharedRes.strings.saved_to_clipboard,
                            it
                        ),
                        duration = Duration.SHORT
                    )
                )
            }
        } ?: run {
            coroutineScope.launch {
                snackbarController.sendEvent(
                    event = SnackbarEvent(
                        message = stringProvider.get(SharedRes.strings.no_ip_available),
                        duration = Duration.SHORT
                    )
                )
            }
        }

    }

    private fun onSaveFilters(filters: ServerQuery) {
        coroutineScope.launch {
            _state.update { it.copy(filter = filters.filter) }
            runCatching {
                saveFiltersUseCase(filters)
                clearServerCache()
            }.onFailure {
                CrashReporter.recordException(it)
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_saving_filters))
            }.onSuccess {
                _uiEvent.send(UiEvent.RefreshList)
            }
        }
    }

    private fun clearFilters() {
        coroutineScope.launch {
            _state.update { it.copy(filter = ServerFilter.ALL) }
            runCatching {
                clearFiltersUseCase()
                clearServerCache()
            }.onFailure {
                CrashReporter.recordException(it)
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_clearing_filters))
            }.onSuccess {
                _uiEvent.send(UiEvent.RefreshList)
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

    private suspend fun clearServerCache() {
        // Ensure cached servers are purged before loading with new filters or query
        runCatching { clearServerCacheUseCase() }
            .onFailure { CrashReporter.recordException(it) }
    }

    private fun updateFilter(filter: ServerFilter) {
        coroutineScope.launch {
            runCatching {
                val current = state.value.filters
                    ?.copy(filter = filter)
                    ?.toDomain(stringProvider)
                    ?: getFiltersUseCase().first()?.copy(filter = filter)
                    ?: ServerQuery(filter = filter)
                saveFiltersUseCase(current)
                clearServerCache()
            }.onSuccess {
                _state.update { it.copy(filter = filter) }
                _uiEvent.send(UiEvent.RefreshList)
            }.onFailure {
                CrashReporter.recordException(it)
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_saving_filters))
            }
        }
    }

    private fun observeConnectivity() {
        connectivityObserver.isConnected
            .onEach { connected ->
                _state.update { it.copy(isConnected = connected) }
            }
            .launchIn(coroutineScope)
    }

    private fun gatherConsent(activity: Any, onAdAvailable: () -> Unit) {
        adsConsentManager.gatherConsent(activity) { error ->
            if (error != null) {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.ads_consent_error))
            } else if (adsConsentManager.canRequestAds) {
                onAdAvailable()
            }
        }
    }
}