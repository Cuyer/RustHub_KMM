package pl.cuyer.rusthub.domain.repository.item.local

import androidx.paging.PagingSource
import database.ItemEntity
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.Language

interface ItemDataSource {
    suspend fun upsertItems(items: List<RustItem>)
    suspend fun clearItems()
    suspend fun isEmpty(language: Language): Boolean
    fun getItemsPagingSource(
        name: String?,
        category: ItemCategory?,
        language: Language,
    ): PagingSource<Int, ItemEntity>
    fun getItemById(id: Long, language: Language): Flow<RustItem?>
}
