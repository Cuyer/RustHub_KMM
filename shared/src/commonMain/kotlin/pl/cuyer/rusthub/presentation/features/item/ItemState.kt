package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemSyncState

import pl.cuyer.rusthub.presentation.model.SearchQueryUi

data class ItemState(
    val isRefreshing: Boolean = true,
    val searchQuery: List<SearchQueryUi> = emptyList(),
    val isLoadingSearchHistory: Boolean = true,
    val selectedCategory: ItemCategory? = null,
    val syncState: ItemSyncState = ItemSyncState.DONE,
)
