package pl.cuyer.rusthub.presentation.features.item

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import pl.cuyer.rusthub.util.catchAndLog
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemSummary
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.domain.usecase.GetPagedItemsUseCase
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.getCurrentAppLanguage
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.domain.usecase.SaveItemSearchQueryUseCase
import pl.cuyer.rusthub.domain.usecase.GetItemSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.DeleteItemSearchQueriesUseCase
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.presentation.model.SearchQueryUi
import pl.cuyer.rusthub.presentation.model.toUi
import kotlin.time.Clock
import pl.cuyer.rusthub.util.AdsConsentManager
import pl.cuyer.rusthub.util.ConnectivityObserver
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ItemViewModel(
    private val getPagedItemsUseCase: GetPagedItemsUseCase,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val saveSearchQueryUseCase: SaveItemSearchQueryUseCase,
    private val getSearchQueriesUseCase: GetItemSearchQueriesUseCase,
    private val deleteSearchQueriesUseCase: DeleteItemSearchQueriesUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val adsConsentManager: AdsConsentManager,
    private val connectivityObserver: ConnectivityObserver,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(ItemState())
    val state = _state
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = _state.value
        )

    private val query = state
        .map { it.query }
        .distinctUntilChanged()

    private val categoryFlow = state
        .map { it.selectedCategory }
        .distinctUntilChanged()

    val showAds = getUserUseCase()
        .map { user -> !(user?.subscribed ?: false) && adsConsentManager.canRequestAds }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = true
        )

    init {
        observeSearchQueries()
        observeConnectivity()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val paging: Flow<PagingData<ItemSummary>> =
        combine(query, categoryFlow) { query, category ->
            Pair(query, category)
        }.flatMapLatest { (query, category) ->
            getPagedItemsUseCase(query, category, getCurrentAppLanguage())
        }
            .cachedIn(coroutineScope)
            .catchAndLog { }

    fun onAction(action: ItemAction) {
        when (action) {
            is ItemAction.OnItemClick -> navigateToItem(action.id, action.name)
            is ItemAction.OnSearch -> handleSearch(action.query)
            is ItemAction.OnCategoryChange -> changeCategory(action.category)
            ItemAction.OnClearSearchQuery -> clearSearchQuery()
            ItemAction.DeleteSearchQueries -> deleteSearchQueries(null)
            is ItemAction.DeleteSearchQueryByQuery -> deleteSearchQueries(action.query)
            is ItemAction.OnError -> sendSnackbarEvent(
                action.exception.toUserMessage(stringProvider) ?: stringProvider.get(
                    SharedRes.strings.error_unknown
                )
            )
        }
    }

    private fun changeCategory(category: ItemCategory?) {
        _state.update { currentState ->
            currentState.copy(
                selectedCategory = category
            )
        }
    }

    private fun navigateToItem(id: Long, name: String) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ItemDetails(id, name)))
        }
    }

    private fun observeSearchQueries() {
        getSearchQueriesUseCase()
            .distinctUntilChanged()
            .map { queries -> queries.map { it.toUi() } }
            .flowOn(Dispatchers.Default)
            .onEach { mappedQueries ->
                updateSearchQueries(mappedQueries)
                updateIsLoadingSearchHistory(false)
            }
            .onStart { updateIsLoadingSearchHistory(true) }
            .catchAndLog {
                sendSnackbarEvent(stringProvider.get(SharedRes.strings.error_fetching_search_history))
            }
            .launchIn(coroutineScope)
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
            sendSnackbarEvent(stringProvider.get(SharedRes.strings.item_query_cannot_be_empty))
        }
    }

    private fun deleteSearchQueries(query: String?) {
        coroutineScope.launch {
            runCatching {
                if (query != null) deleteSearchQueriesUseCase(query) else deleteSearchQueriesUseCase()
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

    private fun clearSearchQuery() {
        _state.update { it.copy(query = "") }
    }

    private fun updateSearchQueries(mappedQueries: List<SearchQueryUi>) {
        _state.update { it.copy(searchQueries = mappedQueries) }
    }

    private fun updateIsLoadingSearchHistory(loading: Boolean) {
        _state.update { it.copy(isLoadingSearchHistory = loading) }
    }

    private fun observeConnectivity() {
        connectivityObserver.isConnected
            .onEach { connected ->
                _state.update { it.copy(isConnected = connected) }
            }
            .launchIn(coroutineScope)
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
