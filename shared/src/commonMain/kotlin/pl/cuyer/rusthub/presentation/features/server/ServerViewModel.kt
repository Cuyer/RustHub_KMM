package pl.cuyer.rusthub.presentation.features.server

import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
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
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.util.catchAndLog
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.CountryRegionMapper
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
    private val stringProvider: StringProvider,
    private val connectivityObserver: ConnectivityObserver,
    private val getUserUseCase: GetUserUseCase,
    private val adsConsentManager: AdsConsentManager,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

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
            initialValue = _state.value
        )

    private val queryFlow = state
        .map { it.query }
        .distinctUntilChanged()

    private val filterChangeFlow = state
        .map { it.filters }
        .distinctUntilChanged()

    val showAds = getUserUseCase()
        .map { user -> !(user?.subscribed ?: false) && adsConsentManager.canRequestAds }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = true
        )

    private var filtersJob: Job? = null
    private var searchQueriesJob: Job? = null
    private var connectivityJob: Job? = null

    @OptIn(ExperimentalCoroutinesApi::class)
    val paging: Flow<PagingData<ServerInfoUi>> =
        combine(queryFlow, filterChangeFlow) { query, filters -> query to filters }
            .flatMapLatest { (query, filters) ->
                getPagedServersUseCase(
                    searchQuery = query,
                    filters = filters?.toDomain(stringProvider) ?: ServerQuery()
                ).map { pagingData ->
                    pagingData.map { it.toUiModel(stringProvider) }
                }.flowOn(Dispatchers.Default)
            }
            .onCompletion { cause ->
                if (cause == null) {
                    _uiEvent.send(UiEvent.OnScrollToIndex(0))
                }
            }
            .cachedIn(coroutineScope)
            .catchAndLog {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_fetching_servers))
            }

    private fun observeSearchQueries() {
        searchQueriesJob?.cancel()

        searchQueriesJob = getSearchQueriesUseCase()
            .distinctUntilChanged()
            .map { searchQuery ->
                searchQuery.map { it.toUi() }
            }
            .flowOn(Dispatchers.Default)
            .onEach { mappedQuery ->
                updateSearchQuery(mappedQuery)
            }
            .catchAndLog {
                sendSnackbarEvent(
                    stringProvider.get(SharedRes.strings.error_fetching_search_history)
                )
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
        filtersJob?.cancel()

        filtersJob = combine(
            getFiltersOptions.invoke(),
            getFiltersUseCase.invoke()
        ) { filtersOptions, filters ->

            if (filtersOptions == null || filtersOptions.maxPlayerCount == 0) {
                return@combine null
            }

            val areFiltersUnset = filters?.playerCount == null &&
                    filters?.groupLimit == null &&
                    filters?.ranking == null
            val effectiveFilters = if (areFiltersUnset) {
                filters?.copy(
                    playerCount = filtersOptions.maxPlayerCount.toLong(),
                    groupLimit = 0,
                    ranking = filtersOptions.maxRanking.toLong()
                )
            } else {
                filters
            }

            effectiveFilters.toUi(
                stringProvider = stringProvider,
                maps = filtersOptions.maps.map { it.displayName },
                flags = filtersOptions.flags.map { it.displayName },
                regions = filtersOptions.regions.map { it.displayName(stringProvider) },
                difficulties = filtersOptions.difficulty.map { it.displayName },
                schedules = filtersOptions.wipeSchedules.map { it.displayName(stringProvider) },
                playerCount = filtersOptions.maxPlayerCount,
                groupLimit = filtersOptions.maxGroupLimit,
                ranking = filtersOptions.maxRanking
            )
        }
            .filterNotNull()
            .distinctUntilChanged()
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
                filters = mappedFilters
            )
        }
    }

    private fun refreshOptions() {
        filtersJob?.cancel()
        observeFilters()
    }

    fun onAction(action: ServerAction) {
        when (action) {
            is ServerAction.OnServerClick -> navigateToServer(action.id, action.name)
            is ServerAction.OnLongServerClick -> saveIpToClipboard(action.ipAddress)
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

            is ServerAction.OnFilterChange -> updateFilter(action.filter)
            is ServerAction.GatherConsent -> gatherConsent(action.activity, action.onAdAvailable)
            is ServerAction.OnDropdownChange -> updateDropdown(action.index, action.selectedIndex)
            is ServerAction.OnCheckboxChange -> updateCheckbox(action.index, action.isChecked)
            is ServerAction.OnRangeChange -> updateRange(action.index, action.value)
            is ServerAction.RefreshOptions -> refreshOptions()
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
                    _state.update { it.copy(query = query) }
                }
            }
        } else {
            sendSnackbarEvent(stringProvider.get(SharedRes.strings.server_query_cannot_be_empty))
        }
    }

    private fun clearSearchQuery() {
        coroutineScope.launch {
            if (_state.value.query.isNotEmpty()) {
                _state.update { it.copy(query = "") }
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
    private fun clearFilters() {
        coroutineScope.launch {
            runCatching {
                clearFiltersUseCase()
            }.onFailure {
                CrashReporter.recordException(it)
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_clearing_filters))
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

    private fun updateDropdown(index: Int, selectedIndex: Int?) {
        _state.update { state ->
            val current = state.filters ?: return@update state
            val updatedLists = current.lists.toMutableList()
            val option = updatedLists.getOrNull(index) ?: return@update state
            if (index == 1) {
                val regionOpt = updatedLists.getOrNull(2)
                val newFlag = selectedIndex?.let { Flag.fromDisplayName(option.options[it]) }
                val region = regionOpt?.selectedIndex?.let { idx ->
                    regionOpt.options.getOrNull(idx)?.let {
                        Region.fromDisplayName(it, stringProvider)
                    }
                }
                val countryRegion = newFlag?.let { CountryRegionMapper.regionForFlag(it) }
                if (region != null && countryRegion != region) {
                    regionOpt?.let { updatedLists[2] = it.copy(selectedIndex = null) }
                }
            }
            if (index == 2) {
                val countryOpt = updatedLists.getOrNull(1)
                val newRegion = selectedIndex?.let {
                    Region.fromDisplayName(option.options[it], stringProvider)
                }
                val flag = countryOpt?.selectedIndex?.let { idx ->
                    countryOpt.options.getOrNull(idx)?.let { Flag.fromDisplayName(it) }
                }
                val flagRegion = flag?.let { CountryRegionMapper.regionForFlag(it) }
                if (newRegion != null && flagRegion != null && flagRegion != newRegion) {
                    countryOpt?.let { updatedLists[1] = it.copy(selectedIndex = null) }
                }
            }
            updatedLists[index] = option.copy(selectedIndex = selectedIndex)
            state.copy(filters = current.copy(lists = updatedLists))
        }
        saveFilters()
    }

    private fun updateCheckbox(index: Int, isChecked: Boolean) {
        _state.update { state ->
            val current = state.filters ?: return@update state
            val updated = current.checkboxes.toMutableList()
            val option = updated.getOrNull(index) ?: return@update state
            updated[index] = option.copy(isChecked = isChecked)
            state.copy(filters = current.copy(checkboxes = updated))
        }
        saveFilters()
    }

    private fun updateRange(index: Int, value: Int?) {
        _state.update { state ->
            val current = state.filters ?: return@update state
            val updated = current.ranges.toMutableList()
            val option = updated.getOrNull(index) ?: return@update state
            updated[index] = option.copy(value = value)
            state.copy(filters = current.copy(ranges = updated))
        }
        saveFilters()
    }

    private fun saveFilters() {
        coroutineScope.launch {
            val current = state.value.filters?.toDomain(stringProvider) ?: return@launch
            runCatching {
                saveFiltersUseCase(current)
            }.onFailure {
                CrashReporter.recordException(it)
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_saving_filters))
            }
        }
    }

    private fun updateFilter(filter: ServerFilter) {
        _state.update { state ->
            val current = state.filters ?: return@update state
            state.copy(filters = current.copy(filter = filter))
        }
        saveFilters()
    }

    private fun observeConnectivity() {
        connectivityJob?.cancel()

        connectivityJob = connectivityObserver.isConnected
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