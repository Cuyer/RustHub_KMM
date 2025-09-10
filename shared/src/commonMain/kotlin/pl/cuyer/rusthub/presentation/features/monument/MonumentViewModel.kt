package pl.cuyer.rusthub.presentation.features.monument

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentSyncState
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.usecase.GetPagedMonumentsUseCase
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentSyncDataSource
import pl.cuyer.rusthub.domain.usecase.SaveMonumentSearchQueryUseCase
import pl.cuyer.rusthub.domain.usecase.GetMonumentSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteMonumentSearchQueriesUseCase
import pl.cuyer.rusthub.presentation.navigation.MonumentDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.util.MonumentsScheduler
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.getCurrentAppLanguage
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.util.catchAndLog
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.util.AdsConsentManager
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.presentation.model.SearchQueryUi
import pl.cuyer.rusthub.presentation.model.toUi
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class MonumentViewModel(
    private val getPagedMonumentsUseCase: GetPagedMonumentsUseCase,
    private val monumentSyncDataSource: MonumentSyncDataSource,
    private val monumentsScheduler: MonumentsScheduler,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val saveSearchQueryUseCase: SaveMonumentSearchQueryUseCase,
    private val getSearchQueriesUseCase: GetMonumentSearchQueriesUseCase,
    private val deleteSearchQueriesUseCase: DeleteMonumentSearchQueriesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val adsConsentManager: AdsConsentManager,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(MonumentState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value,
    )

    private val query = state
        .map { it.query }
        .distinctUntilChanged()

    private val typeFlow = state
        .map { it.selectedType }
        .distinctUntilChanged()

    val showAds = getUserUseCase()
        .map { user -> !(user?.subscribed ?: false) && adsConsentManager.canRequestAds }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = true,
        )

    init {
        observeSyncState()
        observeSearchQueries()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val paging: Flow<PagingData<Monument>> =
        combine(query, typeFlow) { query, type ->
            Pair(query, type)
        }
            .flatMapLatest { (query, type) ->
                getPagedMonumentsUseCase(query, type, getCurrentAppLanguage())
            }
            .cachedIn(coroutineScope)
            .catchAndLog { }

    fun onAction(action: MonumentAction) {
        when (action) {
            is MonumentAction.OnMonumentClick -> navigateToMonument(action.slug)
            is MonumentAction.OnSearch -> handleSearch(action.query)
            is MonumentAction.OnTypeChange -> changeType(action.type)
            MonumentAction.OnRefresh -> refreshMonuments()
            MonumentAction.OnClearSearchQuery -> clearSearchQuery()
            MonumentAction.DeleteSearchQueries -> deleteSearchQueries(null)
            is MonumentAction.DeleteSearchQueryByQuery -> deleteSearchQueries(action.query)
            is MonumentAction.OnError -> sendSnackbarEvent(
                action.exception.toUserMessage(stringProvider)
                    ?: stringProvider.get(SharedRes.strings.error_unknown)
            )
        }
    }

    private fun handleSearch(query: String) {
        if (query.isNotEmpty()) {
            coroutineScope.launch {
                runCatching {
                    saveSearchQueryUseCase(
                        SearchQuery(query = query, timestamp = Clock.System.now(), id = null)
                    )
                }.onFailure {
                    sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_saving_search))
                }.onSuccess {
                    _state.update { it.copy(query = query) }
                }
            }
        } else {
            sendSnackbarEvent(stringProvider.get(SharedRes.strings.monument_query_cannot_be_empty))
        }
    }

    private fun changeType(type: MonumentType?) {
        _state.update { it.copy(selectedType = type) }
    }

    private fun clearSearchQuery() {
        _state.update { it.copy(query = "") }
    }

    private fun deleteSearchQueries(query: String?) {
        coroutineScope.launch {
            runCatching {
                if (query != null) deleteSearchQueriesUseCase(query)
                else deleteSearchQueriesUseCase()
            }.onFailure {
                val msg = if (query != null) {
                    stringProvider.get(SharedRes.strings.error_deleting_query)
                } else {
                    stringProvider.get(SharedRes.strings.error_deleting_queries)
                }
                sendSnackbarEvent(msg)
            }
        }
    }

    private fun observeSearchQueries() {
        getSearchQueriesUseCase()
            .distinctUntilChanged()
            .map { queries -> queries.map { it.toUi() } }
            .flowOn(Dispatchers.Default)
            .onEach { mappedQueries ->
                updateSearchQueries(mappedQueries)
            }
            .catchAndLog {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_fetching_search_history))
            }
            .launchIn(coroutineScope)
    }

    private fun updateSearchQueries(mappedQueries: List<SearchQueryUi>) {
        _state.update { it.copy(searchQueries = mappedQueries) }
    }

    private fun navigateToMonument(slug: String) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(MonumentDetails(slug)))
        }
    }

    private fun observeSyncState() {
        coroutineScope.launch {
            monumentSyncDataSource.observeState().collect { stateValue ->
                stateValue ?: return@collect
                _state.update { current ->
                    current.copy(syncState = stateValue)
                }
            }
        }
    }

    private fun refreshMonuments() {
        coroutineScope.launch {
            monumentSyncDataSource.setState(MonumentSyncState.PENDING)
            monumentsScheduler.startNow()
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
}
