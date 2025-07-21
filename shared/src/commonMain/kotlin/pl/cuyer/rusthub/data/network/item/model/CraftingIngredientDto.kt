package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class CraftingIngredientDto(
    val image: String? = null,
    val name: String? = null,
    val amount: Int? = null,
)
