package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

fun MonumentType.displayName(stringProvider: StringProvider): String = when (this) {
    MonumentType.SMALL -> stringProvider.get(SharedRes.strings.monument_small)
    MonumentType.SAFE_ZONES -> stringProvider.get(SharedRes.strings.monument_safe_zones)
    MonumentType.OCEANSIDE -> stringProvider.get(SharedRes.strings.monument_oceanside)
    MonumentType.MEDIUM -> stringProvider.get(SharedRes.strings.monument_medium)
    MonumentType.ROADSIDE -> stringProvider.get(SharedRes.strings.monument_roadside)
    MonumentType.OFFSHORE -> stringProvider.get(SharedRes.strings.monument_offshore)
    MonumentType.LARGE -> stringProvider.get(SharedRes.strings.monument_large)
}
