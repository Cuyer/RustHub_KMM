package pl.cuyer.rusthub.data.network.item.model

import kotlinx.serialization.Serializable

@Serializable
data class RecyclerDto(
    val image: String? = null,
    val guarantedOutput: List<RecyclerOutputDto>? = null,
    val extraChanceOutput: List<RecyclerOutputDto>? = null,
)
