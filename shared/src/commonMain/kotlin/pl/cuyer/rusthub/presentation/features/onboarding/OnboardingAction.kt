package pl.cuyer.rusthub.presentation.features.onboarding

sealed interface OnboardingAction {
    data object OnContinueAsGuest : OnboardingAction
    data class OnEmailChange(val email: String) : OnboardingAction
    data object OnContinueWithEmail : OnboardingAction
    data object OnGoogleLogin : OnboardingAction
    data object OnShowOtherOptions : OnboardingAction
}
