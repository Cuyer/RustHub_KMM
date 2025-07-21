package pl.cuyer.rusthub.domain.model

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

fun ItemCategory.displayName(stringProvider: StringProvider): String = when (this) {
    ItemCategory.WEAPONS -> stringProvider.get(SharedRes.strings.weapons)
    ItemCategory.AMMO -> stringProvider.get(SharedRes.strings.ammo)
    ItemCategory.CLOTHES -> stringProvider.get(SharedRes.strings.clothes)
    ItemCategory.FUN -> stringProvider.get(SharedRes.strings.fun_category)
    ItemCategory.ITEMS -> stringProvider.get(SharedRes.strings.items)
    ItemCategory.FOOD -> stringProvider.get(SharedRes.strings.food)
    ItemCategory.COMPONENTS -> stringProvider.get(SharedRes.strings.components)
    ItemCategory.MEDICAL -> stringProvider.get(SharedRes.strings.medical)
    ItemCategory.ELECTRICAL -> stringProvider.get(SharedRes.strings.electrical)
    ItemCategory.TOOLS -> stringProvider.get(SharedRes.strings.tools)
    ItemCategory.CONSTRUCTION -> stringProvider.get(SharedRes.strings.construction)
    ItemCategory.RESOURCES -> stringProvider.get(SharedRes.strings.resources)
    ItemCategory.MISC -> stringProvider.get(SharedRes.strings.misc)
    ItemCategory.TRAPS -> stringProvider.get(SharedRes.strings.traps)
    ItemCategory.UNCATEGORIZED -> stringProvider.get(SharedRes.strings.uncategorized)
}
