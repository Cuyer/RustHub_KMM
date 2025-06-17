package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RustMaps(
    @SerialName("barren")
    val barren: Boolean?,
    @SerialName("biomePercentages")
    val biomePercentages: BiomePercentages?,
    @SerialName("iceLakes")
    val iceLakes: Int?,
    @SerialName("islands")
    val islands: Int?,
    @SerialName("mapUrl")
    val mapUrl: String?,
    @SerialName("monumentCount")
    val monumentCount: Int?,
    @SerialName("monumentCounts")
    val monumentCounts: MonumentCounts?,
    @SerialName("mountains")
    val mountains: Int?,
    @SerialName("rivers")
    val rivers: Int?,
    @SerialName("seed")
    val seed: Int?,
    @SerialName("size")
    val size: Int?,
    @SerialName("thumbnailUrl")
    val thumbnailUrl: String?,
    @SerialName("updatedAt")
    val updatedAt: String?,
    @SerialName("url")
    val url: String?
)