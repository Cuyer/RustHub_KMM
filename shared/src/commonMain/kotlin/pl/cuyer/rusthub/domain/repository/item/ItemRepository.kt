package pl.cuyer.rusthub.domain.repository.item

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemsResponse
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.RustItem

interface ItemRepository {
    suspend fun getItems(): Result<List<RustItem>>
    fun getItems(
        page: Int,
        size: Int,
        category: ItemCategory?,
        language: Language,
        searchQuery: String?
    ): Flow<Result<ItemsResponse>>
    fun getItemDetails(
        slug: String,
        language: Language,
    ): Flow<Result<RustItem>>
}
