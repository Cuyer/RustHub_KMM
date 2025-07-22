package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import androidx.compose.runtime.Immutable

@Immutable
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
        Region.ASIA -> stringProvider.get(SharedRes.strings.asia)
        Region.EUROPE -> stringProvider.get(SharedRes.strings.europe)
        Region.AMERICA -> stringProvider.get(SharedRes.strings.america)
        Region.AFRICA -> stringProvider.get(SharedRes.strings.africa)
        Region.SOUTH_AMERICA -> stringProvider.get(SharedRes.strings.south_america)
        Region.OCEANIA -> stringProvider.get(SharedRes.strings.oceania)
        Region.AUSTRALIA -> stringProvider.get(SharedRes.strings.australia)
    }