package pl.cuyer.rusthub.data.network.filtersOptions.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RegionDto {
    ASIA,
    EUROPE,

    @SerialName("NORTH AMERICA")
    NORTH_AMERICA,
    AFRICA,

    @SerialName("SOUTH AMERICA")
    SOUTH_AMERICA,
    OCEANIA,
    AUSTRALIA
}