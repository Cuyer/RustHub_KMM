package pl.cuyer.rusthub.presentation.features.settings

import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Theme
import androidx.compose.runtime.Immutable

@Immutable
data class SettingsState(
    val username: String? = null,
    val showSubscriptionDialog: Boolean = false,
    val provider: AuthProvider? = null,
    val subscribed: Boolean = false,
    val anonymousExpiration: String? = null,
    val isLoading: Boolean = false,
    val theme: Theme = Theme.SYSTEM,
    val dynamicColors: Boolean = false,
    val useSystemColors: Boolean = true,
)
