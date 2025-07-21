package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
enum class ItemCategoryDto {
    WEAPONS,
    AMMO,
    CLOTHES,
    FUN,
    ITEMS,
    FOOD,
    COMPONENTS,
    MEDICAL,
    ELECTRICAL,
    TOOLS,
    CONSTRUCTION,
    RESOURCES,
    MISC,
    TRAPS,
    UNCATEGORIZED
}
