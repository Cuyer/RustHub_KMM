package pl.cuyer.rusthub.presentation.features.auth.password

import androidx.compose.runtime.Immutable

@Immutable
sealed interface ChangePasswordAction {
    @Immutable
    data object OnChange : ChangePasswordAction
    @Immutable
    data class OnOldPasswordChange(val password: String) : ChangePasswordAction
    @Immutable
    data class OnNewPasswordChange(val password: String) : ChangePasswordAction
}
