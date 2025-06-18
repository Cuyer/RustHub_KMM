package pl.cuyer.rusthub.data.network.filtersOptions.model

import kotlinx.serialization.Serializable

@Serializable
enum class MapsDto {
    CUSTOM,
    PROCEDURAL,
    BARREN,
    CRAGGY_ISLAND,
    HAPPIS_ISLAND,
    SAVAS_ISLAND_KOTH,
    SAVAS_ISLAND
}