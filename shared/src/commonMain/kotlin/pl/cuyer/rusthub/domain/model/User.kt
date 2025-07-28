package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class User(
    val email: String?,
    val username: String,
    val accessToken: String,
    val refreshToken: String?,
    val obfuscatedId: String?,
    val provider: AuthProvider,
    val subscribed: Boolean,
    val emailConfirmed: Boolean,
)
