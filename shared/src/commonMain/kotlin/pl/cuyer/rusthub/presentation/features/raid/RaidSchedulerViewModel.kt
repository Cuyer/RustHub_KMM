package pl.cuyer.rusthub.presentation.features.raid

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.BaseViewModel
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.usecase.DeleteRaidsUseCase
import pl.cuyer.rusthub.domain.usecase.ObserveRaidsUseCase
import pl.cuyer.rusthub.domain.usecase.SaveRaidUseCase
import pl.cuyer.rusthub.presentation.snackbar.Duration
import pl.cuyer.rusthub.presentation.snackbar.SnackbarAction
import pl.cuyer.rusthub.presentation.snackbar.SnackbarController
import pl.cuyer.rusthub.presentation.snackbar.SnackbarEvent
import pl.cuyer.rusthub.util.StringProvider

class RaidSchedulerViewModel(
    observeRaidsUseCase: ObserveRaidsUseCase,
    private val saveRaidUseCase: SaveRaidUseCase,
    private val deleteRaidsUseCase: DeleteRaidsUseCase,
    private val snackbarController: SnackbarController,
    private val stringProvider: StringProvider,
) : BaseViewModel() {

    private var recentlyDeleted: List<Raid> = emptyList()

    private val _state = MutableStateFlow(RaidSchedulerState())
    val state = _state
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000L),
            initialValue = RaidSchedulerState()
        )

    init {
        observeRaidsUseCase()
            .onEach { raids -> _state.update { it.copy(raids = raids) } }
            .catch { }
            .launchIn(coroutineScope)
    }

    fun onAction(action: RaidSchedulerAction) {
        when (action) {
            RaidSchedulerAction.OnAddClick -> _state.update {
                it.copy(showForm = true, editingRaid = null)
            }
            is RaidSchedulerAction.OnRaidLongClick -> toggleSelection(action.id)
            is RaidSchedulerAction.OnRaidSwiped -> deleteRaids(listOf(action.id))
            is RaidSchedulerAction.OnMoveRaid -> moveRaid(action.from, action.to)
            RaidSchedulerAction.OnDeleteSelected -> deleteRaids(_state.value.selectedIds.toList())
            RaidSchedulerAction.OnEditSelected -> {
                val raid = _state.value.raids.firstOrNull { it.id in _state.value.selectedIds }
                _state.update { it.copy(showForm = true, editingRaid = raid) }
            }
            is RaidSchedulerAction.OnSaveRaid -> saveRaid(action.raid)
            RaidSchedulerAction.OnDismissForm -> _state.update { it.copy(showForm = false, editingRaid = null) }
        }
    }

    private fun toggleSelection(id: String) {
        _state.update { state ->
            val new = state.selectedIds.toMutableSet()
            if (!new.add(id)) {
                new.remove(id)
            }
            state.copy(selectedIds = new)
        }
    }

    private fun saveRaid(raid: Raid) {
        coroutineScope.launch {
            saveRaidUseCase(raid)
            _state.update { it.copy(showForm = false, editingRaid = null) }
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
            state.copy(raids = raids)
        }
    }

    private fun deleteRaids(ids: List<String>) {
        if (ids.isEmpty()) return
        val raidsToDelete = _state.value.raids.filter { it.id in ids }
        coroutineScope.launch {
            recentlyDeleted = raidsToDelete
            deleteRaidsUseCase(ids)
            _state.update { it.copy(selectedIds = emptySet()) }
            snackbarController.sendEvent(
                SnackbarEvent(
                    message = stringProvider.get(SharedRes.strings.raids_deleted, ids.size),
                    action = SnackbarAction(stringProvider.get(SharedRes.strings.undo)) {
                        coroutineScope.launch {
                            recentlyDeleted.forEach { saveRaidUseCase(it) }
                            recentlyDeleted = emptyList()
                        }
                    },
                    duration = Duration.SHORT
                )
            )
        }
    }
}
