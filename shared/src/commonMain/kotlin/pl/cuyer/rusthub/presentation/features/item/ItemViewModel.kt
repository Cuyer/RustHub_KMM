package pl.cuyer.rusthub.presentation.features.item

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
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.ItemSyncState
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.presentation.navigation.ItemDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.domain.usecase.GetPagedItemsUseCase
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource

class ItemViewModel(
    private val getPagedItemsUseCase: GetPagedItemsUseCase,
    private val itemSyncDataSource: ItemSyncDataSource,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")
    private val categoryFlow = MutableStateFlow<ItemCategory?>(null)

    private val _state = MutableStateFlow(ItemState())
    val state = _state
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = ItemState()
        )

    init {
        observeSyncState()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val paging: Flow<PagingData<RustItem>> =
        combine(queryFlow, categoryFlow) { query, category ->
            Pair(query, category)
        }.flatMapLatest { (query, category) ->
            getPagedItemsUseCase(query, category)
        }
            .flowOn(Dispatchers.Default)
            .cachedIn(coroutineScope)
            .catch { }

    fun onAction(action: ItemAction) {
        when (action) {
            is ItemAction.OnItemClick -> navigateToItem(action.id)
            is ItemAction.OnSearch -> queryFlow.update { action.query }
            is ItemAction.OnCategoryChange -> changeCategory(action.category)
            ItemAction.OnClearSearchQuery -> queryFlow.update { "" }
            is ItemAction.OnError -> Unit
        }
    }

    private fun changeCategory(category: ItemCategory?) {
        categoryFlow.update {
            category
        }
        _state.update { currentState ->
            currentState.copy(
                selectedCategory = category
            )
        }
    }

    private fun navigateToItem(id: Long) {
        coroutineScope.launch {
            _uiEvent.send(UiEvent.Navigate(ItemDetails(id)))
        }
    }

    private fun observeSyncState() {
        coroutineScope.launch {
            itemSyncDataSource.observeState().collect { stateValue ->
                stateValue ?: return@collect
                _state.update { current ->
                    current.copy(syncState = stateValue)
                }
            }
        }
    }
}
