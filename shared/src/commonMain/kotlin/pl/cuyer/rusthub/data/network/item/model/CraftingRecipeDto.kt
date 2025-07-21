package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CraftingRecipeDto(
    val ingredients: List<CraftingIngredientDto>? = null,
    @SerialName("output_amount") val outputAmount: Int? = null,
    @SerialName("output_image") val outputImage: String? = null,
    @SerialName("output_name") val outputName: String? = null,
)
