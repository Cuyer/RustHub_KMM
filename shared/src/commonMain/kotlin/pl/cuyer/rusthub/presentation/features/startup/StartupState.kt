package pl.cuyer.rusthub.presentation.features.startup

import androidx.navigation3.runtime.NavKey
import pl.cuyer.rusthub.domain.model.Theme
import pl.cuyer.rusthub.presentation.navigation.Onboarding
import androidx.compose.runtime.Immutable

/** State for determining the app start destination */
@Immutable
data class StartupState(
    val startDestination: NavKey = Onboarding,
    val isLoading: Boolean = true,
    val theme: Theme = Theme.SYSTEM,
    val dynamicColors: Boolean = false,
)
