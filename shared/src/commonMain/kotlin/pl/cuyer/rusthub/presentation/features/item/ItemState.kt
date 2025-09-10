package pl.cuyer.rusthub.presentation.features.item

import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.presentation.model.SearchQueryUi
import androidx.compose.runtime.Immutable

@Immutable
data class ItemState(
    val searchQueries: List<SearchQueryUi> = emptyList(),
    val selectedCategory: ItemCategory? = null,
    val isConnected: Boolean = true,
    val query: String = "",
)
