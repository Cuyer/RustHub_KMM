package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

enum class Region {
    ASIA,
    EUROPE,
    AMERICA,
    AFRICA,
    SOUTH_AMERICA,
    OCEANIA,
    AUSTRALIA;

    companion object {
        fun fromDisplayName(name: String, stringProvider: StringProvider): Region? =
            entries.firstOrNull { it.displayName(stringProvider) == name }
    }
}

fun Region.displayName(stringProvider: StringProvider): String =
    when (this) {
        ASIA -> stringProvider.get(SharedRes.strings.asia)
        EUROPE -> stringProvider.get(SharedRes.strings.europe)
        AMERICA -> stringProvider.get(SharedRes.strings.america)
        AFRICA -> stringProvider.get(SharedRes.strings.africa)
        SOUTH_AMERICA -> stringProvider.get(SharedRes.strings.south_america)
        OCEANIA -> stringProvider.get(SharedRes.strings.oceania)
        AUSTRALIA -> stringProvider.get(SharedRes.strings.australia)
    }