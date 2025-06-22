package pl.cuyer.rusthub.presentation.features.onboarding

sealed interface OnboardingAction {
    data object OnLoginClick : OnboardingAction
    data object OnRegisterClick : OnboardingAction
    data object OnContinueAsGuest : OnboardingAction
}
