package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.data.network.item.model.ItemCategoryDto
import pl.cuyer.rusthub.data.network.item.model.ItemLanguageDto
import pl.cuyer.rusthub.data.network.item.model.LootingDto
import pl.cuyer.rusthub.data.network.item.model.CraftingDto
import pl.cuyer.rusthub.data.network.item.model.RecyclingDto
import pl.cuyer.rusthub.data.network.item.model.RaidingDto

@Serializable
data class RustItemDto(
    val slug: String? = null,
    val url: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    @SerialName("stack_size") val stackSize: Int? = null,
    val health: Int? = null,
    val categories: List<ItemCategoryDto>? = null,
    val looting: List<LootingDto>? = null,
    val crafting: CraftingDto? = null,
    val recycling: RecyclingDto? = null,
    val raiding: List<RaidingDto>? = null,
    @SerialName("short_name") val shortName: String? = null,
    val id: String? = null,
    @SerialName("icon_url") val iconUrl: String? = null,
    val language: ItemLanguageDto? = null,
)
