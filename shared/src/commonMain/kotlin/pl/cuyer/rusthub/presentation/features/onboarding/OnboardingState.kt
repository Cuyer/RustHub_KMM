package pl.cuyer.rusthub.presentation.features.onboarding

import androidx.compose.runtime.Immutable

@Immutable
data class OnboardingState(
    val isLoading: Boolean = false,
    val googleLoading: Boolean = false,
    val continueAsGuestLoading: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val showOtherOptions: Boolean = false
)
