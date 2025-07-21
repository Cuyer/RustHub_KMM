package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class RecyclingDto(
    val radtownRecycler: RecyclerDto? = null,
    val safezoneRecycler: RecyclerDto? = null,
)
