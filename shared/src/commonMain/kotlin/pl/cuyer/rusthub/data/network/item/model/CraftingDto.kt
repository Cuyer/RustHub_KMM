package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class CraftingDto(
    val craftingRecipe: CraftingRecipeDto? = null,
    val researchTableCost: ResearchTableCostDto? = null,
    val techTreeCost: TechTreeCostDto? = null,
)
