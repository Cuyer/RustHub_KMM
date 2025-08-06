package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TableRecipeDto(
    @SerialName("table_image") val tableImage: String? = null,
    @SerialName("table_name") val tableName: String? = null,
    val ingredients: List<TableRecipeIngredientDto>? = null,
    @SerialName("output_image") val outputImage: String? = null,
    @SerialName("output_name") val outputName: String? = null,
    @SerialName("output_amount") val outputAmount: Int? = null,
    @SerialName("total_cost") val totalCost: List<TableRecipeIngredientDto>? = null,
)

@Serializable
data class TableRecipeIngredientDto(
    val image: String? = null,
    val name: String? = null,
    val amount: Int? = null,
)
