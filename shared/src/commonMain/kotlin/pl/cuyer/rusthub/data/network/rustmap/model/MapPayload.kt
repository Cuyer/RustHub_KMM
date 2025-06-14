package pl.cuyer.domain.models.server.rustmap

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MapPayload(
    @SerialName("imageIconUrl") val url: String
)