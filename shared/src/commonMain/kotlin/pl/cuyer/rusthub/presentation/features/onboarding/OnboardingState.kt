package pl.cuyer.rusthub.presentation.features.onboarding

data class OnboardingState(
    val isLoading: Boolean = false,
    val email: String = "",
    val emailError: String? = null,
    val showOtherOptions: Boolean = false
)
