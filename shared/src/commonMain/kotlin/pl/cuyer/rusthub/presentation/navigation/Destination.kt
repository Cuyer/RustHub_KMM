package pl.cuyer.rusthub.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object Onboarding : NavKey


@Serializable
data class Credentials(
    val email: String,
    val exists: Boolean
) : NavKey

@Serializable
data object ServerList : NavKey

@Serializable
data object Settings : NavKey

@Serializable
data object ChangePassword : NavKey

@Serializable
data object DeleteAccount : NavKey

@Serializable
data class ServerDetails(
    val id: Long,
    val name: String
) : NavKey

@Serializable
data object PrivacyPolicy : NavKey
