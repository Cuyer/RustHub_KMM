package pl.cuyer.rusthub.presentation.features.startup

import androidx.navigation3.runtime.NavKey
import pl.cuyer.rusthub.presentation.navigation.Onboarding

/** State for determining the app start destination */
data class StartupState(
    val startDestination: NavKey = Onboarding,
    val isLoading: Boolean = true,
)
