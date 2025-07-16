package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

enum class WipeSchedule {
    WEEKLY,
    BIWEEKLY,
    MONTHLY;

    companion object {
        fun fromDisplayName(displayName: String, stringProvider: StringProvider): WipeSchedule? =
            entries.firstOrNull { it.displayName(stringProvider) == displayName }
    }
}

fun WipeSchedule.displayName(stringProvider: StringProvider): String =
    when (this) {
        WipeSchedule.WEEKLY -> stringProvider.get(SharedRes.strings.weekly)
        WipeSchedule.BIWEEKLY -> stringProvider.get(SharedRes.strings.biweekly)
        WipeSchedule.MONTHLY -> stringProvider.get(SharedRes.strings.monthly)
    }