package pl.cuyer.rusthub.presentation.features.settings

import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.presentation.model.SubscriptionPlan
import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val username: String? = null,
    val showSubscriptionDialog: Boolean = false,
    val provider: AuthProvider? = null,
    val subscribed: Boolean = false,
    val currentPlan: SubscriptionPlan? = null,
    val subscriptionExpiration: String? = null,
    val subscriptionStatus: String? = null,
    val anonymousExpiration: String? = null,
    val isLoading: Boolean = false,
    val isLoggingOut: Boolean = false,
    val isPrivacyOptionsRequired: Boolean = false,
    val theme: Theme = Theme.SYSTEM,
    val dynamicColors: Boolean = false,
    val useSystemColors: Boolean = true,
    val currentSubscription: ActiveSubscription? = null,
    val currentUser: User? = null,
    val isConnected: Boolean = true,
)
