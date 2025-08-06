package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RegionDto {
    ASIA,
    EUROPE,
    @SerialName("NORTH AMERICA")
    NORTH_AMERICA,
    AFRICA,
    OCEANIA,
    AUSTRALIA
}