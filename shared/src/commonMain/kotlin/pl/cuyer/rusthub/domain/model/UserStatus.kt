package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class UserStatus(
    val emailConfirmed: Boolean,
    val subscribed: Boolean,
)
