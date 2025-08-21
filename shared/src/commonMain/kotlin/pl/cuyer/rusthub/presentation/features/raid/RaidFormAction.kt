package pl.cuyer.rusthub.presentation.features.raid

import pl.cuyer.rusthub.domain.model.SteamUser

sealed interface RaidFormAction {
    data class OnNameChange(val value: String) : RaidFormAction
    data class OnDateTimeChange(val value: String) : RaidFormAction
    data object OnSelectTargetClick : RaidFormAction
    data class OnSearchQueryChange(val value: String) : RaidFormAction
    data object OnSearchUser : RaidFormAction
    data class OnUserSelected(val user: SteamUser) : RaidFormAction
    data object OnDismissSearch : RaidFormAction
    data class OnDescriptionChange(val value: String) : RaidFormAction
    data object OnSave : RaidFormAction
}
