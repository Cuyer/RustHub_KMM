package pl.cuyer.rusthub.data.network.server.model

import domain.models.server.battlemetrics.BattlemetricsServerContent
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.domain.models.server.battlemetrics.Links

@Serializable
data class BattlemetricsPage(
    @SerialName("data")
    val `data`: List<BattlemetricsServerContent> = emptyList(),
    @SerialName("links")
    val links: Links?
)
