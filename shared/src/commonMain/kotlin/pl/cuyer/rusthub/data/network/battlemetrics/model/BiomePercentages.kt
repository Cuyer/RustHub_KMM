package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class BiomePercentages(
    @SerialName("d")
    val d: Double?,
    @SerialName("f")
    val f: Double?,
    @SerialName("j")
    val j: Double?,
    @SerialName("s")
    val s: Double?,
    @SerialName("t")
    val t: Double?
)