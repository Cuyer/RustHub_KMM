package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.RustItem

data class ItemDetailsState(
    val item: RustItem? = null,
    val isLoading: Boolean = true,
    val itemId: Long? = null,
)
