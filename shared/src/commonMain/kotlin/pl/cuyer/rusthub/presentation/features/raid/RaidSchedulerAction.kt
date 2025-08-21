package pl.cuyer.rusthub.presentation.features.raid

sealed interface RaidSchedulerAction {
    data object OnAddClick : RaidSchedulerAction
    data class OnRaidLongClick(val id: String) : RaidSchedulerAction
    data class OnRaidSwiped(val id: String) : RaidSchedulerAction
    data class OnMoveRaid(val from: Int, val to: Int) : RaidSchedulerAction
    data object OnDeleteSelected : RaidSchedulerAction
    data object OnEditSelected : RaidSchedulerAction
}
