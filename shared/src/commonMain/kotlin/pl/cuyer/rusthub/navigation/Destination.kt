package pl.cuyer.rusthub.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object HomeGraph : Destination

    @Serializable
    data object Home : Destination
}