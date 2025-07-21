package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResearchTableCostDto(
    @SerialName("table_image") val tableImage: String? = null,
    @SerialName("table_name") val tableName: String? = null,
    @SerialName("item_image") val itemImage: String? = null,
    @SerialName("item_name") val itemName: String? = null,
    @SerialName("item_amount") val itemAmount: Int? = null,
    @SerialName("scrap_image") val scrapImage: String? = null,
    @SerialName("scrap_name") val scrapName: String? = null,
    @SerialName("scrap_amount") val scrapAmount: Int? = null,
    @SerialName("output_image") val outputImage: String? = null,
    @SerialName("output_name") val outputName: String? = null,
)
