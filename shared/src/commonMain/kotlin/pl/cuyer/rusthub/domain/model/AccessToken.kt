package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class AccessToken(
    val accessToken: String,
    val username: String,
    val provider: AuthProvider,
    val subscribed: Boolean,
    val obfuscatedId: String?,
)
