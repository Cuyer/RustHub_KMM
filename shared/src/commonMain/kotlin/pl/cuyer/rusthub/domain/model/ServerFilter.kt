package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

enum class ServerFilter {
    ALL,
    FAVOURITES,
    SUBSCRIBED;

    companion object {
        fun fromDisplayName(displayName: String, stringProvider: StringProvider): ServerFilter? {
            return entries.firstOrNull { it.displayName(stringProvider) == displayName }
        }
    }
}

fun ServerFilter.displayName(stringProvider: StringProvider): String =
    when (this) {
        ServerFilter.ALL -> stringProvider.get(SharedRes.strings.all)
        ServerFilter.FAVOURITES -> stringProvider.get(SharedRes.strings.favourites)
        ServerFilter.SUBSCRIBED -> stringProvider.get(SharedRes.strings.subscribed)
    }
