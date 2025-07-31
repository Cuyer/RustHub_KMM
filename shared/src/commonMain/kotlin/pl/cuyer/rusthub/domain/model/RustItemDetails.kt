package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

@Serializable
@Immutable
data class LootAmount(val min: Int? = null, val max: Int? = null)

@Serializable
@Immutable
data class Looting(
    val from: String? = null,
    val image: String? = null,
    val chance: Double? = null,
    val amount: LootAmount? = null
)

@Serializable
@Immutable
data class LootContent(
    val spawn: String? = null,
    val image: String? = null,
    val stack: LootAmount? = null,
    val chance: Double? = null,
    val amount: LootAmount? = null,
)

@Serializable
@Immutable
data class WhereToFind(
    val place: String? = null,
    val image: String? = null,
    val amount: LootAmount? = null,
)

@Serializable
@Immutable
data class CraftingIngredient(
    val image: String? = null,
    val name: String? = null,
    val amount: Int? = null
)

@Serializable
@Immutable
data class CraftingRecipe(
    val ingredients: List<CraftingIngredient>? = null,
    val outputAmount: Int? = null,
    val outputImage: String? = null,
    val outputName: String? = null
)

@Serializable
@Immutable
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
@Immutable
data class TechTreeCost(
    val workbenchImage: String? = null,
    val workbenchName: String? = null,
    val scrapImage: String? = null,
    val scrapName: String? = null,
    val scrapAmount: Int? = null,
    val outputName: String? = null
)

@Serializable
@Immutable
data class Crafting(
    val craftingRecipe: CraftingRecipe? = null,
    val researchTableCost: ResearchTableCost? = null,
    val techTreeCost: TechTreeCost? = null
)

@Serializable
@Immutable
data class RecyclerOutput(
    val image: String? = null,
    val name: String? = null,
    val amount: Double? = null
)

@Serializable
@Immutable
data class Recycler(
    val image: String? = null,
    val guarantedOutput: List<RecyclerOutput>? = null,
    val extraChanceOutput: List<RecyclerOutput>? = null
)

@Serializable
@Immutable
data class Recycling(
    val radtownRecycler: Recycler? = null,
    val safezoneRecycler: Recycler? = null
)

@Serializable
@Immutable
data class StartingItem(
    val icon: String? = null,
    val name: String? = null
)

@Serializable
@Immutable
data class RaidItem(
    val icon: String? = null,
    val name: String? = null,
    val amount: Int? = null
)

@Serializable
@Immutable
data class RaidResource(
    val icon: String? = null,
    val name: String? = null,
    val amount: Int? = null,
    val mixingTableAmount: Int? = null
)

@Serializable
@Immutable
data class Raiding(
    val startingItem: StartingItem? = null,
    val timeToRaid: Int? = null,
    val amount: List<RaidItem>? = null,
    val rawMaterialCost: List<RaidResource>? = null
)

@Serializable
@Immutable
enum class ItemSyncState { PENDING, DONE }
