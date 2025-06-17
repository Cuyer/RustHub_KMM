package pl.cuyer.rusthub.data.network.server.model


import domain.models.server.battlemetrics.Rates
import domain.models.server.battlemetrics.Wipe
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.data.network.util.serializers.FlexibleFloatSerializer

@Serializable
data class RustSettings(
    @SerialName("blueprints")
    val blueprints: Boolean?,
    @SerialName("decay")
    @Serializable(with = FlexibleFloatSerializer::class)
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