package pl.cuyer.rusthub.presentation.features.auth.password

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ResetPasswordAction {
    @Immutable
    data object OnSend : ResetPasswordAction
    @Immutable
    data class OnEmailChange(val email: String) : ResetPasswordAction
}
