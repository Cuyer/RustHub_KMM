package pl.cuyer.rusthub.data.network.monument.model

import kotlinx.serialization.Serializable

@Serializable
data class MonumentsResponseDto(
    val monuments: List<MonumentDto>
)
