package pl.cuyer.rusthub.presentation.navigation

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan

@Serializable
@Immutable
data object Onboarding : NavKey


@Serializable
@Immutable
data class Credentials(
    val email: String,
    val exists: Boolean,
    val provider: AuthProvider? = null
) : NavKey

@Serializable
@Immutable
data object ServerList : NavKey

@Serializable
@Immutable
data object Settings : NavKey

@Serializable
@Immutable
data object ChangePassword : NavKey

@Serializable
@Immutable
data object DeleteAccount : NavKey

@Serializable
@Immutable
data object UpgradeAccount : NavKey

@Serializable
@Immutable
data object ConfirmEmail : NavKey

@Serializable
@Immutable
data class ServerDetails(
    val id: Long,
    val name: String
) : NavKey

@Serializable
@Immutable
data object ItemList : NavKey

@Serializable
@Immutable
data class ItemDetails(
    val id: Long,
    val name: String? = null,
) : NavKey

@Serializable
@Immutable
data object MonumentList : NavKey

@Serializable
@Immutable
data class MonumentDetails(
    val slug: String,
) : NavKey

@Serializable
@Immutable
data object RaidScheduler : NavKey

@Serializable
@Immutable
data class RaidForm(val raid: Raid? = null) : NavKey

@Serializable
@Immutable
data object PrivacyPolicy : NavKey

@Serializable
@Immutable
data class ResetPassword(val email: String) : NavKey

@Serializable
@Immutable
data object Terms : NavKey

@Serializable
@Immutable
data object About : NavKey

@Serializable
@Immutable
data class Subscription(val plan: SubscriptionPlan? = null) : NavKey
