package pl.cuyer.rusthub.domain.repository.item.local

import androidx.paging.PagingSource
import database.ItemEntity
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemSummary
import pl.cuyer.rusthub.domain.model.Language

interface ItemDataSource {
    suspend fun upsertItems(items: List<ItemSummary>, language: Language)
    suspend fun clearItems()
    suspend fun isEmpty(language: Language): Boolean
    fun getItemsPagingSource(
        name: String?,
        category: ItemCategory?,
        language: Language,
    ): PagingSource<Int, ItemEntity>
}
