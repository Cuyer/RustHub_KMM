package pl.cuyer.rusthub.data.network.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserInfoDto(
    val username: String,
    val email: String? = null,
    val provider: String,
    val emailConfirmed: Boolean,
    val subscribed: Boolean,
    val obfuscatedId: String? = null,
    val favourites: List<String> = emptyList(),
    val subscriptions: List<String> = emptyList()
)
