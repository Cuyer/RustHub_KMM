package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class AccessTokenDto(
    val accessToken: String,
    val username: String,
    val provider: String,
    val subscribed: Boolean = false
)
