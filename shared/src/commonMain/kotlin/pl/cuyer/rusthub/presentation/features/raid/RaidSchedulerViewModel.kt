package pl.cuyer.rusthub.presentation.features.raid

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.UNLIMITED
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.domain.usecase.DeleteRaidsUseCase
import pl.cuyer.rusthub.domain.usecase.ObserveRaidsUseCase
import pl.cuyer.rusthub.domain.usecase.SaveRaidUseCase
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
import pl.cuyer.rusthub.util.AlarmScheduler

class RaidSchedulerViewModel(
    observeRaidsUseCase: ObserveRaidsUseCase,
    private val deleteRaidsUseCase: DeleteRaidsUseCase,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
    private val saveRaidUseCase: SaveRaidUseCase,
    private val searchSteamUserUseCase: SearchSteamUserUseCase,
    private val alarmScheduler: AlarmScheduler,
) : BaseViewModel() {

    private var recentlyDeleted: List<Raid> = emptyList()

    private val _uiEvent = Channel<UiEvent>(UNLIMITED)
    val uiEvent = _uiEvent.receiveAsFlow()

    private val _state = MutableStateFlow(RaidSchedulerState())
    val state = _state.stateIn(
        scope = coroutineScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = RaidSchedulerState()
    )

    init {
        observeRaidsUseCase()
            .onEach { raids ->
                _state.update { it.copy(raids = raids) }
                val missing = raids.flatMap { it.steamIds }
                    .filter { _state.value.users[it] == null }
                    .distinct()
                if (missing.isNotEmpty()) {
                    searchSteamUserUseCase(missing)
                        .onEach { result ->
                            if (result is Result.Success) {
                                _state.update { state ->
                                    val fetched = result.data.associateBy { it.steamId }
                                    val notFound = missing.filter { it !in fetched.keys }.associateWith { null }
                                    state.copy(users = state.users + fetched + notFound)
                                }
                            }
                        }
                        .launchIn(coroutineScope)
                }
            }
            .catch { }
            .launchIn(coroutineScope)
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
        coroutineScope.launch {
            recentlyDeleted = raidsToDelete
            raidsToDelete.forEach { alarmScheduler.cancel(it) }
            deleteRaidsUseCase(ids)
            _state.update { it.copy(selectedIds = emptySet()) }
            snackbarController.sendEvent(
                SnackbarEvent(
                    message = stringProvider.get(SharedRes.strings.raids_deleted, ids.size),
                    action = SnackbarAction(stringProvider.get(SharedRes.strings.undo)) {
                        coroutineScope.launch {
                            recentlyDeleted.forEach {
                                saveRaidUseCase(it)
                                alarmScheduler.cancel(it)
                                alarmScheduler.schedule(it)
                            }
                            recentlyDeleted = emptyList()
                        }
                    },
                    duration = Duration.SHORT
                )
            )
        }
    }
}
