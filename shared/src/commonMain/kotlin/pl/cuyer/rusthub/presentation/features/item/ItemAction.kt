package pl.cuyer.rusthub.presentation.features.item

import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.ItemCategory

@Immutable
sealed interface ItemAction {
    @Immutable
    data class OnItemClick(val id: Long) : ItemAction
    @Immutable
    data class OnSearch(val query: String) : ItemAction
    @Immutable
    data class OnCategoryChange(val category: ItemCategory?) : ItemAction
    @Immutable
    data class OnError(val exception: Throwable) : ItemAction
    @Immutable
    data object OnRefresh : ItemAction
    @Immutable
    data object OnClearSearchQuery : ItemAction
    @Immutable
    data object DeleteSearchQueries : ItemAction
    @Immutable
    data class DeleteSearchQueryByQuery(val query: String) : ItemAction
}
