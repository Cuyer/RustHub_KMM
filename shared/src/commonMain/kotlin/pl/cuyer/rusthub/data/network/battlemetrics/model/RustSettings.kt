package domain.models.server.battlemetrics


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RustSettings(
    @SerialName("blueprints")
    val blueprints: Boolean?,
    @SerialName("decay")
    val decay: Float?,
    @SerialName("forceWipeType")
    val forceWipeType: String?,
    @SerialName("groupLimit")
    val groupLimit: Int?,
    @SerialName("kits")
    val kits: Boolean?,
    @SerialName("rates")
    val rates: Rates?,
    @SerialName("teamUILimit")
    val teamUILimit: Int?,
    @SerialName("upkeep")
    val upkeep: Double?,
    @SerialName("wipes")
    val wipes: List<Wipe> = emptyList(),
    @SerialName("timeZone")
    val timezone: String?
)