package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.data.network.item.model.ItemCategoryDto
import pl.cuyer.rusthub.data.network.item.model.ItemLanguageDto
import pl.cuyer.rusthub.data.network.item.model.LootingDto
import pl.cuyer.rusthub.data.network.item.model.CraftingDto
import pl.cuyer.rusthub.data.network.item.model.RecyclingDto
import pl.cuyer.rusthub.data.network.item.model.RaidingDto
import pl.cuyer.rusthub.data.network.item.model.LootContentDto
import pl.cuyer.rusthub.data.network.item.model.WhereToFindDto
import pl.cuyer.rusthub.data.network.item.model.TableRecipeDto

@Serializable
data class RustItemDto(
    val slug: String? = null,
    val url: String? = null,
    val name: String? = null,
    val description: String? = null,
    val image: String? = null,
    @SerialName("stack_size") val stackSize: Int? = null,
    val health: Int? = null,
    val attributes: Map<String, String>? = null,
    val categories: List<ItemCategoryDto>? = null,
    val looting: List<LootingDto>? = null,
    @SerialName("loot_contents") val lootContents: List<LootContentDto>? = null,
    @SerialName("where_to_find") val whereToFind: List<WhereToFindDto>? = null,
    val crafting: CraftingDto? = null,
    @SerialName("table_recipe") val tableRecipe: TableRecipeDto? = null,
    val recycling: RecyclingDto? = null,
    val raiding: List<RaidingDto>? = null,
    @SerialName("short_name") val shortName: String? = null,
    val id: Long? = null,
    @SerialName("icon_url") val iconUrl: String? = null,
    val language: ItemLanguageDto? = null,
)
