package pl.cuyer.domain.models.server.rustmap

import kotlinx.serialization.Serializable

@Serializable
data class MapResponse(
    val data: MapPayload
)