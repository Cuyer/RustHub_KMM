package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UserExistsResponseDto(
    val exists: Boolean
)

