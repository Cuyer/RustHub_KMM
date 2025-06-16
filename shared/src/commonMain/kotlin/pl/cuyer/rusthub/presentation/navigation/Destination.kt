package pl.cuyer.rusthub.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {

    @Serializable
    data object ServerList : Destination

    @Serializable
    data object ServerDetails : Destination
}