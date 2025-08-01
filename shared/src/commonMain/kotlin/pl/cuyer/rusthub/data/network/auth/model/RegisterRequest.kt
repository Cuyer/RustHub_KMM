package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)
