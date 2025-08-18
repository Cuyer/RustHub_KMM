package pl.cuyer.rusthub.presentation.features.raid

import pl.cuyer.rusthub.domain.model.Raid

sealed interface RaidSchedulerAction {
    data object OnAddClick : RaidSchedulerAction
    data class OnRaidLongClick(val id: String) : RaidSchedulerAction
    data class OnRaidSwiped(val id: String) : RaidSchedulerAction
    data class OnMoveRaid(val from: Int, val to: Int) : RaidSchedulerAction
    data object OnDeleteSelected : RaidSchedulerAction
    data object OnEditSelected : RaidSchedulerAction
    data class OnSaveRaid(val raid: Raid) : RaidSchedulerAction
    data object OnDismissForm : RaidSchedulerAction
}
