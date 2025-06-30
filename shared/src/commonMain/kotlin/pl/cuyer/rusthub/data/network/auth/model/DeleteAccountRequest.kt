package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class DeleteAccountRequest(
    val username: String,
    val password: String
)
