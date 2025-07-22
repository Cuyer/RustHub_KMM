package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.RustItem
import androidx.compose.runtime.Immutable

@Immutable
data class ItemDetailsState(
    val item: RustItem? = null,
    val isLoading: Boolean = true,
    val itemId: Long? = null,
)
