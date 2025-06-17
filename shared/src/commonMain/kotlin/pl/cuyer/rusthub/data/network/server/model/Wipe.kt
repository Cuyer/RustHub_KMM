package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Wipe(
    @SerialName("days")
    val days: List<String> = emptyList(),
    @SerialName("hour")
    val hour: Int?,
    @SerialName("minute")
    val minute: Int?,
    @SerialName("type")
    val type: String?,
    @SerialName("weeks")
    val weeks: List<Int> = emptyList()
)