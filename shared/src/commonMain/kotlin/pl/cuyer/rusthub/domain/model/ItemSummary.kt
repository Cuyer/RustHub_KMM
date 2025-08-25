package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.domain.model.ItemCategory


@Serializable
@Immutable
data class ItemSummary(
    val id: Long,
    val name: String? = null,
    @SerialName("short_name")
    val shortName: String? = null,
    val image: String? = null,
    val categories: List<ItemCategory> = emptyList(),
)
