package pl.cuyer.rusthub.data.network.battlemetrics.model.error

import kotlinx.serialization.Serializable

@Serializable
data class Error(
    val code: String,
    val detail: String,
    val meta: Meta,
    val title: String
)