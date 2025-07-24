package pl.cuyer.rusthub.presentation.features.onboarding

import androidx.compose.runtime.Immutable

@Immutable
sealed interface OnboardingAction {
    @Immutable
    data object OnContinueAsGuest : OnboardingAction
    @Immutable
    data class OnEmailChange(val email: String) : OnboardingAction
    @Immutable
    data object OnContinueWithEmail : OnboardingAction
    @Immutable
    data object OnGoogleLogin : OnboardingAction
    @Immutable
    data object OnShowOtherOptions : OnboardingAction
}
