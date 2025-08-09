package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.map
import androidx.paging.PagingData
import androidx.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.data.local.mapper.toRustItem
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.ItemRepository
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.repository.item.ItemRemoteMediator

/**
 * Use case for retrieving paginated items from the local database or remote source.
 *
 * This class is part of the domain layer and adheres to clean architecture principles.
 * It uses Paging3 for pagination and supports filtering by query, category, and language.
 * The `updatedLanguage` variable is used to handle a specific limitation where the Polish language
 * is not supported in the API. If the provided language is Polish, it is replaced with English
 * to ensure compatibility with the API.
 *
 * @property dataSource The data source for retrieving items.
 * @property json The `Json` instance for JSON serialization and deserialization.
 */
class GetPagedItemsUseCase(
    private val dataSource: ItemDataSource,
    private val api: ItemRepository,
    private val json: Json
) {
    /**
     * Invokes the use case to retrieve a paginated flow of `RustItem` objects.
     *
     * @param query The optional search query to filter items by name.
     * @param category The optional category to filter items.
     * @param language The language to filter items. If Polish is provided, it is replaced with English.
     * @return A `Flow` emitting paginated data of `RustItem` objects.
     */
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        query: String?,
        category: ItemCategory?,
        language: Language,
    ): Flow<PagingData<RustItem>> {
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true
            ),
            remoteMediator = ItemRemoteMediator(
                dataSource,
                api,
                category,
                updatedLanguage,
                query
            ),
            pagingSourceFactory = {
                dataSource.getItemsPagingSource(query, category, updatedLanguage)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toRustItem(json) }
        }
    }
}