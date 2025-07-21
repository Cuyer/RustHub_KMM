package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class LootAmount(val min: Int? = null, val max: Int? = null)

@Serializable
data class Looting(
    val from: String? = null,
    val image: String? = null,
    val chance: Double? = null,
    val amount: LootAmount? = null
)

@Serializable
data class CraftingIngredient(
    val image: String? = null,
    val name: String? = null,
    val amount: Int? = null
)

@Serializable
data class CraftingRecipe(
    val ingredients: List<CraftingIngredient>? = null,
    val outputAmount: Int? = null,
    val outputImage: String? = null,
    val outputName: String? = null
)

@Serializable
data class ResearchTableCost(
    val tableImage: String? = null,
    val tableName: String? = null,
    val itemImage: String? = null,
    val itemName: String? = null,
    val itemAmount: Int? = null,
    val scrapImage: String? = null,
    val scrapName: String? = null,
    val scrapAmount: Int? = null,
    val outputImage: String? = null,
    val outputName: String? = null
)

@Serializable
data class TechTreeCost(
    val workbenchImage: String? = null,
    val workbenchName: String? = null,
    val scrapImage: String? = null,
    val scrapName: String? = null,
    val scrapAmount: Int? = null,
    val outputName: String? = null
)

@Serializable
data class Crafting(
    val craftingRecipe: CraftingRecipe? = null,
    val researchTableCost: ResearchTableCost? = null,
    val techTreeCost: TechTreeCost? = null
)

@Serializable
data class RecyclerOutput(
    val image: String? = null,
    val name: String? = null,
    val amount: Double? = null
)

@Serializable
data class Recycler(
    val image: String? = null,
    val guarantedOutput: List<RecyclerOutput>? = null,
    val extraChanceOutput: List<RecyclerOutput>? = null
)

@Serializable
data class Recycling(
    val radtownRecycler: Recycler? = null,
    val safezoneRecycler: Recycler? = null
)

@Serializable
data class StartingItem(
    val icon: String? = null,
    val name: String? = null
)

@Serializable
data class RaidItem(
    val icon: String? = null,
    val name: String? = null,
    val amount: Int? = null
)

@Serializable
data class RaidResource(
    val icon: String? = null,
    val name: String? = null,
    val amount: Int? = null,
    val mixingTableAmount: Int? = null
)

@Serializable
data class Raiding(
    val startingItem: StartingItem? = null,
    val timeToRaid: Int? = null,
    val amount: List<RaidItem>? = null,
    val rawMaterialCost: List<RaidResource>? = null
)

@Serializable
enum class ItemSyncState { PENDING, DONE }
