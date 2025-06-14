package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BattlemetricsApiResponse(
    @SerialName("data")
    val content: List<BattlemetricsServerContent>
)