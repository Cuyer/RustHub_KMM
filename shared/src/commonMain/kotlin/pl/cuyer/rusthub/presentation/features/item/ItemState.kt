package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemSyncState

data class ItemState(
    val isRefreshing: Boolean = true,
    val searchQuery: String = "",
    val selectedCategory: ItemCategory? = null,
    val syncState: ItemSyncState = ItemSyncState.DONE,
)
