package pl.cuyer.rusthub.data.local.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class RegionEntity {
    ASIA,
    EUROPE,
    @SerialName("NORTH AMERICA")
    AMERICA,
    AFRICA,
    @SerialName("SOUTH AMERICA")
    SOUTH_AMERICA,
    @SerialName("OCEANIA")
    OCEANIA,
    @SerialName("AUSTRALIA")
    AUSTRALIA
}

