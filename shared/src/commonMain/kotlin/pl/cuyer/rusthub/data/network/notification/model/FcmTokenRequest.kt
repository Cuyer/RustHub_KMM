package pl.cuyer.rusthub.data.network.notification.model

import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    val token: String
)
