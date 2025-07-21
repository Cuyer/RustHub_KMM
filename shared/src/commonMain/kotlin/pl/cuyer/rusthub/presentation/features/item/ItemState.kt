package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.ItemCategory

data class ItemState(
    val isRefreshing: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: ItemCategory? = null,
)
