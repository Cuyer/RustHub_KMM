package pl.cuyer.rusthub.presentation.navigation

import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

@Serializable
data object Onboarding : NavKey


@Serializable
@Immutable
data class Credentials(
    val email: String,
    val exists: Boolean,
    val provider: AuthProvider? = null
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
data object UpgradeAccount : NavKey

@Serializable
data object ConfirmEmail : NavKey

@Serializable
@Immutable
data class ServerDetails(
    val id: Long,
    val name: String
) : NavKey

@Serializable
data object ItemList : NavKey

@Serializable
@Immutable
data class ItemDetails(
    val id: Long,
) : NavKey

@Serializable
data object PrivacyPolicy : NavKey

@Serializable
@Immutable
data class ResetPassword(val email: String) : NavKey

@Serializable
data object Terms : NavKey

@Serializable
data object About : NavKey

@Serializable
@Immutable
data class Subscription(val plan: SubscriptionPlan? = null) : NavKey
