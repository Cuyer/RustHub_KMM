package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class UserExistsInfo(
    val exists: Boolean,
    val provider: AuthProvider? = null
)
