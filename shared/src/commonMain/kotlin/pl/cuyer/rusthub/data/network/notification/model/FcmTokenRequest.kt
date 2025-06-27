package pl.cuyer.rusthub.data.network.notification.model

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class FcmTokenRequest(
    val token: String,
    val timestamp: Instant
)
