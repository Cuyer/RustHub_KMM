package pl.cuyer.rusthub.domain.repository.item.local

import app.cash.paging.PagingSource
import database.ItemEntity
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem

interface ItemDataSource {
    suspend fun upsertItems(items: List<RustItem>)
    suspend fun isEmpty(): Boolean
    fun getItemsPagingSource(
        name: String?,
        category: ItemCategory?
    ): PagingSource<Int, ItemEntity>
    fun getItemById(id: Long): Flow<RustItem?>
}
