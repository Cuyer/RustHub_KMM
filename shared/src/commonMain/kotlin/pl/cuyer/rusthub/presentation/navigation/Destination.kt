package pl.cuyer.rusthub.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Onboarding : NavKey

@Serializable
data object Login : NavKey

@Serializable
data object Register : NavKey

@Serializable
data object ServerList : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data class ServerDetails(
    val id: Long,
    val name: String
) : NavKey
