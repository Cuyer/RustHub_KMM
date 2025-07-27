package pl.cuyer.rusthub.data.network.user.model

import kotlinx.serialization.Serializable

@Serializable
data class UserDto(
    val emailConfirmed: Boolean
)
