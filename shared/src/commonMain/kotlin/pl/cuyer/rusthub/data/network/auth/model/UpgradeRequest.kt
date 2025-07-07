package pl.cuyer.rusthub.data.network.auth.model

import kotlinx.serialization.Serializable

@Serializable
data class UpgradeRequest(
    val username: String? = null,
    val password: String? = null,
    val email: String? = null,
    val token: String? = null,
)
