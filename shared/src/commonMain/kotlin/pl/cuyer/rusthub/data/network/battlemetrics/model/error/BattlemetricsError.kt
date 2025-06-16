package pl.cuyer.rusthub.data.network.battlemetrics.model.error

import kotlinx.serialization.Serializable

@Serializable
data class BattlemetricsError(
    val errors: List<Error>
)