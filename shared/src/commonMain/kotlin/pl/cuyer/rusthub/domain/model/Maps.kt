package pl.cuyer.rusthub.domain.model

enum class Maps {
    CUSTOM,
    PROCEDURAL,
    BARREN,
    CRAGGY_ISLAND,
    HAPPIS_ISLAND,
    SAVAS_ISLAND_KOTH,
    SAVAS_ISLAND
}

val Maps.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }