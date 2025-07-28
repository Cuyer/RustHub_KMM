package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class UserStatus(
    val emailConfirmed: Boolean,
    val subscribed: Boolean,
)
