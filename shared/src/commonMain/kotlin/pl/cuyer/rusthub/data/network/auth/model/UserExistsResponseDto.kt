package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.domain.model.AuthProvider

@Serializable
data class UserExistsResponseDto(
    val exists: Boolean,
    val provider: AuthProvider? = null
)

