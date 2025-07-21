package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class ItemCategoryDto {
    @SerialName("Weapons")
    WEAPONS,
    @SerialName("Ammo")
    AMMO,
    @SerialName("Clothes")
    CLOTHES,
    @SerialName("Fun")
    FUN,
    @SerialName("Items")
    ITEMS,
    @SerialName("Food")
    FOOD,
    @SerialName("Components")
    COMPONENTS,
    @SerialName("Medical")
    MEDICAL,
    @SerialName("Electrical")
    ELECTRICAL,
    @SerialName("Tools")
    TOOLS,
    @SerialName("Construction")
    CONSTRUCTION,
    @SerialName("Resources")
    RESOURCES,
    @SerialName("Misc")
    MISC,
    @SerialName("Traps")
    TRAPS,
    @SerialName("Uncategorized")
    UNCATEGORIZED
}
