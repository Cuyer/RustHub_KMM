package pl.cuyer.rusthub.domain.model

enum class Region {
    ASIA,
    EUROPE,
    AMERICA,
    AFRICA,
    SOUTH_AMERICA,
    OCEANIA,
    AUSTRALIA;

    companion object {
        fun fromDisplayName(name: String): Region? =
            Region.entries.firstOrNull { it.displayName == name }
    }
}

val Region.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }