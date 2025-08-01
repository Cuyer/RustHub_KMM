package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class TokenPairDto(
    val accessToken: String,
    val refreshToken: String,
    val username: String,
    val email: String,
    val provider: String,
    val subscribed: Boolean = false,
    val emailConfirmed: Boolean? = null,
    val obfuscatedId: String? = null,
)
