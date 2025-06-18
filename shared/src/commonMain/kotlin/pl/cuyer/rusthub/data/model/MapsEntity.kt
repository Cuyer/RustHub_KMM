package pl.cuyer.rusthub.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class MapsEntity {
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

