package pl.cuyer.rusthub.presentation.features.raid

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.usecase.DeleteRaidUseCase
import pl.cuyer.rusthub.domain.usecase.GetRaidsUseCase
import pl.cuyer.rusthub.domain.usecase.CreateRaidUseCase
import pl.cuyer.rusthub.domain.usecase.ObserveRaidsUseCase
import pl.cuyer.rusthub.domain.usecase.SearchSteamUserUseCase
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.presentation.navigation.RaidForm
import pl.cuyer.rusthub.presentation.navigation.UiEvent
import pl.cuyer.rusthub.presentation.navigation.UiEvent.*
import pl.cuyer.rusthub.util.StringProvider
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.ConnectivityException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.util.AlarmScheduler
import pl.cuyer.rusthub.util.ConnectivityObserver
import pl.cuyer.rusthub.util.catchAndLog
import pl.cuyer.rusthub.util.toUserMessage

class RaidSchedulerViewModel(
    private val getRaidsUseCase: GetRaidsUseCase,
    private val observeRaidsUseCase: ObserveRaidsUseCase,
    private val deleteRaidUseCase: DeleteRaidUseCase,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val createRaidUseCase: CreateRaidUseCase,
    private val searchSteamUserUseCase: SearchSteamUserUseCase,
    private val alarmScheduler: AlarmScheduler,
    private val connectivityObserver: ConnectivityObserver,
) : BaseViewModel() {

    private var recentlyDeleted: List<Raid> = emptyList()

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(RaidSchedulerState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = _state.value
    )

    private var loadJob: Job? = null
    private var deleteJob: Job? = null
    private var searchJob: Job? = null

    init {
        observeConnectivity()
        observeRaids()
    }

    fun onAction(action: RaidSchedulerAction) {
        when (action) {
            RaidSchedulerAction.OnAddClick -> coroutineScope.launch {
                _uiEvent.send(Navigate(RaidForm()))
            }
            is RaidSchedulerAction.OnRaidLongClick -> toggleSelection(action.id)
            is RaidSchedulerAction.OnRaidSwiped -> deleteRaids(listOf(action.id))
            is RaidSchedulerAction.OnMoveRaid -> moveRaid(action.from, action.to)
            RaidSchedulerAction.OnDeleteSelected -> deleteRaids(_state.value.selectedIds.toList())
            RaidSchedulerAction.OnEditSelected -> editSelected()
            RaidSchedulerAction.OnRefresh -> loadRaids()
            is RaidSchedulerAction.OnNavigateToRaid -> navigateToRaid(raid = action.raid)
        }
    }

    private fun editSelected() {
        coroutineScope.launch {
            val raid = _state.value.raids.firstOrNull { it.id in _state.value.selectedIds }
            if (raid != null) {
                _uiEvent.send(Navigate(RaidForm(raid)))
            }
        }
    }

    private fun navigateToRaid(raid: Raid) {
        coroutineScope.launch {
            _uiEvent.send(Navigate(RaidForm(raid)))
        }
    }

    private fun toggleSelection(id: String) {
        _state.update { state ->
            val new = state.selectedIds.toMutableSet()
            if (!new.add(id)) {
                new.remove(id)
            }
            state.copy(selectedIds = new.toSet())
        }
    }

    private fun moveRaid(from: Int, to: Int) {
        _state.update { state ->
            val raids = state.raids.toMutableList()
            if (from in raids.indices) {
                val raid = raids.removeAt(from)
                val target = to.coerceIn(0, raids.size)
                raids.add(target, raid)
            }
            state.copy(raids = raids.toList())
        }
    }

    private fun deleteRaids(ids: List<String>) {
        if (ids.isEmpty()) return
        val raidsToDelete = _state.value.raids.filter { it.id in ids }
        deleteJob?.cancel()
        deleteJob = coroutineScope.launch {
            recentlyDeleted = raidsToDelete
            raidsToDelete.forEach { alarmScheduler.cancel(it) }
            runCatching {
                ids.map { id ->
                    async {
                        deleteRaidUseCase(id).collect { result ->
                            if (result is Result.Error) throw result.exception
                        }
                    }
                }.awaitAll()
                _state.update { it.copy(selectedIds = emptySet(), raids = it.raids.filterNot { raid -> raid.id in ids }) }
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = stringProvider.get(SharedRes.strings.raids_deleted, ids.size),
                        action = SnackbarAction(stringProvider.get(SharedRes.strings.undo)) {
                            coroutineScope.launch {
                                runCatching {
                                    recentlyDeleted.map { raid ->
                                        async {
                                            createRaidUseCase(raid).collect { res ->
                                                if (res is Result.Error) throw res.exception
                                            }
                                            alarmScheduler.cancel(raid)
                                            alarmScheduler.schedule(raid)
                                        }
                                    }.awaitAll()
                                    recentlyDeleted = emptyList()
                                    loadRaids()
                                }.onFailure { e ->
                                    val cause = (e as? CancellationException)?.cause ?: e
                                    coroutineScope.launch {
                                        snackbarController.sendEvent(
                                            SnackbarEvent(
                                                message = cause.toUserMessage(stringProvider)
                                                    ?: stringProvider.get(SharedRes.strings.error_unknown),
                                                duration = Duration.SHORT,
                                            )
                                        )
                                    }
                                }
                            }
                        },
                        duration = Duration.LONG,
                    )
                )
            }.onFailure { e ->
                val cause = (e as? CancellationException)?.cause ?: e
                coroutineScope.launch {
                    snackbarController.sendEvent(
                        SnackbarEvent(
                            message = cause.toUserMessage(stringProvider)
                                ?: stringProvider.get(SharedRes.strings.error_unknown),
                            duration = Duration.SHORT,
                        )
                    )
                }
            }
        }
    }

    private fun observeRaids() {
        observeRaidsUseCase()
            .onEach { raids ->
                _state.update { it.copy(raids = raids) }
                val ids = raids.flatMap { it.steamIds }.distinct()
                if (ids.isNotEmpty()) {
                    searchJob?.cancel()
                    searchJob = coroutineScope.launch {
                        searchSteamUserUseCase(ids)
                            .collectLatest { res ->
                                ensureActive()
                                if (res is Result.Success) {
                                    _state.update { state ->
                                        val fetched = res.data.associateBy { it.steamId }
                                        val notFound = ids
                                            .filter { it !in fetched.keys }
                                            .associateWith { null }
                                        state.copy(users = state.users + fetched + notFound)
                                    }
                                }
                            }
                    }
                }
            }
            .catchAndLog { e ->
                snackbarController.sendEvent(
                    SnackbarEvent(
                        message = e.toUserMessage(stringProvider)
                            ?: stringProvider.get(SharedRes.strings.error_unknown),
                        duration = Duration.SHORT,
                    )
                )
            }
            .launchIn(coroutineScope)
    }

    private fun loadRaids() {
        loadJob?.cancel()
        searchJob?.cancel()
        loadJob = coroutineScope.launch {
            getRaidsUseCase()
                .onStart { _state.update { it.copy(isRefreshing = true, hasError = false) } }
                .catchAndLog { e ->
                    _state.update { it.copy(isRefreshing = false, hasError = true) }
                    snackbarController.sendEvent(
                        SnackbarEvent(
                            message = e.toUserMessage(stringProvider)
                                ?: stringProvider.get(SharedRes.strings.error_unknown),
                            duration = Duration.SHORT,
                        )
                    )
                }
                .collectLatest { result ->
                    ensureActive()
                    when (result) {
                        is Result.Success -> {
                            delay(300)
                            _state.update { it.copy(isRefreshing = false, hasError = false) }
                        }

                        is Result.Error -> {
                            if (result.exception is ConnectivityException ||
                                result.exception is ServiceUnavailableException) {
                                _state.update { it.copy(isRefreshing = false, hasError = false) }
                                return@collectLatest
                            } else {
                                _state.update { it.copy(isRefreshing = false, hasError = true) }
                                snackbarController.sendEvent(
                                    SnackbarEvent(
                                        message = result.exception.toUserMessage(stringProvider)
                                            ?: stringProvider.get(SharedRes.strings.error_unknown),
                                        duration = Duration.SHORT,
                                    )
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun observeConnectivity() {
        connectivityObserver.isConnected
            .onEach { connected ->
                val wasDisconnected = state.value.isConnected.not() && connected
                _state.update { it.copy(isConnected = connected) }
                if (wasDisconnected) {
                    loadRaids()
                }
            }
            .launchIn(coroutineScope)
    }
}
