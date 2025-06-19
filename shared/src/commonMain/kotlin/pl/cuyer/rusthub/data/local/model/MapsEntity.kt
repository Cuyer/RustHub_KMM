package pl.cuyer.rusthub.data.local.model

import kotlinx.serialization.Serializable

@Serializable
enum class MapsEntity {
    CUSTOM,
    PROCEDURAL,
    BARREN,
    CRAGGY_ISLAND,
    HAPPIS_ISLAND,
    SAVAS_ISLAND_KOTH,
    SAVAS_ISLAND
}

