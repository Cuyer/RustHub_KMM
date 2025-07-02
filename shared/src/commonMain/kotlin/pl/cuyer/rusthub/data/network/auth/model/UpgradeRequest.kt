package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UpgradeRequest(
    val username: String,
    val email: String,
    val password: String
)
