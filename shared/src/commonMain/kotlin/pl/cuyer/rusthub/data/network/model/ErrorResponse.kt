package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    val message: String? = null,
    val cause: String? = null,
)
