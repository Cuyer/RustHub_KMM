package pl.cuyer.rusthub.presentation.features.auth.upgrade

import androidx.compose.runtime.Immutable

@Immutable
sealed interface UpgradeAction {
    @Immutable
    data object OnSubmit : UpgradeAction
    @Immutable
    data class OnEmailChange(val email: String) : UpgradeAction
    @Immutable
    data class OnUsernameChange(val username: String) : UpgradeAction
    @Immutable
    data class OnPasswordChange(val password: String) : UpgradeAction
    @Immutable
    data object OnNavigateUp : UpgradeAction
    @Immutable
    data object OnGoogleLogin : UpgradeAction
}
