package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MapsDto {
    CUSTOM,
    PROCEDURAL,
    BARREN,

    @SerialName("CRAGGY ISLAND")
    CRAGGY_ISLAND,

    @SerialName("HAPPIS ISLAND")
    HAPPIS_ISLAND,

    @SerialName("SAVAS ISLAND KOTH")
    SAVAS_ISLAND_KOTH,

    @SerialName("SAVAS ISLAND")
    SAVAS_ISLAND
}