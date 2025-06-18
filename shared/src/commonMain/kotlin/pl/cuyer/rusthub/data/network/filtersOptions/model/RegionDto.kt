package pl.cuyer.rusthub.data.network.filtersOptions.model

import kotlinx.serialization.Serializable

@Serializable
enum class RegionDto {
    ASIA,
    EUROPE,
    AMERICA,
    AFRICA,
    SOUTH_AMERICA,
    OCEANIA,
    AUSTRALIA
}