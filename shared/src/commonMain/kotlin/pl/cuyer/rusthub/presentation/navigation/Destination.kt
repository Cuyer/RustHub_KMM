package pl.cuyer.rusthub.presentation.navigation

import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable
    data object HomeGraph : Destination

    @Serializable
    data object Home : Destination
}