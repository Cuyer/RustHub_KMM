package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Rates(
    @SerialName("component")
    val component: Float?,
    @SerialName("craft")
    val craft: Float?,
    @SerialName("gather")
    val gather: Float?,
    @SerialName("scrap")
    val scrap: Float?
)