package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class TokenPair(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val email: String,
    val provider: AuthProvider,
    val subscribed: Boolean,
)
