package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.ItemCategory

sealed interface ItemAction {
    data class OnItemClick(val id: Long) : ItemAction
    data class OnSearch(val query: String) : ItemAction
    data class OnCategoryChange(val category: ItemCategory?) : ItemAction
    data class OnError(val exception: Throwable) : ItemAction
    data object OnClearSearchQuery : ItemAction
}
