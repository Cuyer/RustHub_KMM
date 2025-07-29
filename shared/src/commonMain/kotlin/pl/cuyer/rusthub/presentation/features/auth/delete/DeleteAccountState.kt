package pl.cuyer.rusthub.presentation.features.auth.delete

import pl.cuyer.rusthub.domain.model.AuthProvider
import androidx.compose.runtime.Immutable

@Immutable
data class DeleteAccountState(
    val password: String = "",
    val passwordError: String? = null,
    val provider: AuthProvider? = null,
    val isLoading: Boolean = false,
    val hasSubscription: Boolean = false,
    val showSubscriptionDialog: Boolean = false
)
