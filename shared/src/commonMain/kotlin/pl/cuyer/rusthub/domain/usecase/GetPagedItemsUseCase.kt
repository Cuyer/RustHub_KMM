package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.data.local.mapper.toRustItem
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.domain.model.Language

class GetPagedItemsUseCase(
    private val dataSource: ItemDataSource,
    private val json: Json
) {
    operator fun invoke(
        query: String?,
        category: ItemCategory?,
        language: Language,
    ): Flow<PagingData<RustItem>> {
        // If the language is Polish, we switch to English for the query
        // This is a workaround for the issue with Polish language not being supported in the API
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }
        return Pager(
            config = PagingConfig(
                pageSize = 40,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                dataSource.getItemsPagingSource(query, category, updatedLanguage)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toRustItem(json) }
        }
    }
}
