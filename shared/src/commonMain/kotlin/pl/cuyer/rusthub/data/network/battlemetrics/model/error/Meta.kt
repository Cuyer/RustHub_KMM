package pl.cuyer.rusthub.data.network.battlemetrics.model.error

import kotlinx.serialization.Serializable

@Serializable
data class Meta(
    val allow: Boolean,
    val available: Int,
    val limit: Int,
    val period: Int,
    val tryAgain: String,
    val used: Int
)