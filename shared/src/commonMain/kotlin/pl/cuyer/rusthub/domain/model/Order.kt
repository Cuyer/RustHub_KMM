package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import androidx.compose.runtime.Immutable

@Immutable
enum class Order {
    WIPE,
    RANK,
    PLAYER_COUNT,
    NEXT_WIPE;

    companion object {
        fun fromDisplayName(displayName: String, stringProvider: StringProvider): Order? {
            return Order.entries.firstOrNull { it.displayName(stringProvider) == displayName }
        }
    }
}

fun Order.displayName(stringProvider: StringProvider): String =
    when (this) {
        Order.WIPE -> stringProvider.get(SharedRes.strings.last_wipe)
        Order.RANK -> stringProvider.get(SharedRes.strings.ranking)
        Order.PLAYER_COUNT -> stringProvider.get(SharedRes.strings.player_count)
        Order.NEXT_WIPE -> stringProvider.get(SharedRes.strings.closest_wipe)
    }
