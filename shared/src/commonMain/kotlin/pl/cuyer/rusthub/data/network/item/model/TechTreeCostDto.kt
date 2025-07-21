package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TechTreeCostDto(
    @SerialName("workbench_image") val workbenchImage: String? = null,
    @SerialName("workbench_name") val workbenchName: String? = null,
    @SerialName("scrap_image") val scrapImage: String? = null,
    @SerialName("scrap_name") val scrapName: String? = null,
    @SerialName("scrap_amount") val scrapAmount: Int? = null,
    @SerialName("output_name") val outputName: String? = null,
)
