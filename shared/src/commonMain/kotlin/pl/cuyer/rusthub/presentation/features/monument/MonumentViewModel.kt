package pl.cuyer.rusthub.presentation.features.monument

import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentSyncState
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.usecase.GetPagedMonumentsUseCase
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentSyncDataSource
import pl.cuyer.rusthub.presentation.navigation.MonumentDetails
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.MonumentsScheduler
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.util.getCurrentAppLanguage
import pl.cuyer.rusthub.util.toUserMessage
import pl.cuyer.rusthub.domain.usecase.GetUserUseCase
import pl.cuyer.rusthub.util.AdsConsentManager

class MonumentViewModel(
    private val getPagedMonumentsUseCase: GetPagedMonumentsUseCase,
    private val monumentSyncDataSource: MonumentSyncDataSource,
    private val monumentsScheduler: MonumentsScheduler,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val getUserUseCase: GetUserUseCase,
    private val adsConsentManager: AdsConsentManager,
) : BaseViewModel() {

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val queryFlow = MutableStateFlow("")
    private val typeFlow = MutableStateFlow<MonumentType?>(null)

    private val _state = MutableStateFlow(MonumentState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = MonumentState(),
    )

    val showAds = getUserUseCase()
        .map { user -> !(user?.subscribed ?: false) && adsConsentManager.canRequestAds }
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = true,
        )

    init {
        observeSyncState()
    }

    @kotlinx.coroutines.ExperimentalCoroutinesApi
    val paging: Flow<PagingData<Monument>> =
        combine(queryFlow, typeFlow) { query, type ->
            Pair(query, type)
        }.flatMapLatest { (query, type) ->
            getPagedMonumentsUseCase(query, type, getCurrentAppLanguage())
        }.cachedIn(coroutineScope)

    fun onAction(action: MonumentAction) {
        when (action) {
            is MonumentAction.OnMonumentClick -> navigateToMonument(action.slug)
            is MonumentAction.OnSearch -> {
                queryFlow.value = action.query
                _state.update { it.copy(searchText = action.query) }
            }
            is MonumentAction.OnTypeChange -> {
                typeFlow.value = action.type
                _state.update { it.copy(selectedType = action.type) }
            }
            MonumentAction.OnRefresh -> refreshMonuments()
        }
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
}
