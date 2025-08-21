package pl.cuyer.rusthub.presentation.features.raid

sealed interface RaidFormAction {
    data class OnNameChange(val value: String) : RaidFormAction
    data class OnDateTimeChange(val value: String) : RaidFormAction
    data object OnSelectTargetClick : RaidFormAction
    data class OnSearchQueryChange(val value: String) : RaidFormAction
    data object OnSearchUser : RaidFormAction
    data class OnToggleFoundUser(val id: String) : RaidFormAction
    data object OnAddFoundUsers : RaidFormAction
    data object OnDismissSearch : RaidFormAction
    data class OnDescriptionChange(val value: String) : RaidFormAction
    data object OnSave : RaidFormAction
}
