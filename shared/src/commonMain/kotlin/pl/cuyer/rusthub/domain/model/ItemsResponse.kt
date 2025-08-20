package pl.cuyer.rusthub.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable

@Serializable
@Immutable
/**
 * Represents a paged response of [RustItem] objects returned from the API.
 */
data class ItemsResponse(
    val page: Int,
    val size: Int,
    @SerialName("total_pages")
    val totalPages: Int,
    @SerialName("total_items")
    val totalItems: Long,
    val items: List<RustItem>
)
