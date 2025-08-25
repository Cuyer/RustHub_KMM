package pl.cuyer.rusthub.data.local.item

import androidx.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import database.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.ItemSummary
import pl.cuyer.rusthub.data.local.model.LanguageEntity
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource

class ItemDataSourceImpl(
    db: RustHubDatabase,
) : ItemDataSource, Queries(db) {

    override suspend fun upsertItems(items: List<ItemSummary>, language: Language) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.transaction {
                    items.forEach { item ->
                        queries.upsertItem(
                            id = item.id,
                            name = item.name,
                            shortName = item.shortName,
                            image = item.image,
                            categories = item.categories.joinToString(",") { it.name },
                            language = language.toEntity()
                        )
                    }
                }
            }
        }
    }

    override suspend fun clearItems() {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.deleteItems()
            }
        }
    }

    override suspend fun isEmpty(language: Language): Boolean {
        return withContext(Dispatchers.IO) {
            safeQuery(true) {
                val updatedLanguage = if (language == Language.POLISH) {
                    Language.ENGLISH
                } else {
                    language
                }
                queries.countItems(language = updatedLanguage.toEntity()).executeAsOne() == 0L
            }
        }
    }

    override fun getItemsPagingSource(
        name: String?,
        category: ItemCategory?,
        language: Language,
    ): PagingSource<Int, ItemEntity> {
        val query = name.orEmpty()
        val isMultiLanguage = query.isNotBlank() && language != Language.ENGLISH
        return QueryPagingSource(
            countQuery = if (isMultiLanguage) {
                queries.countPagedItemsFilteredMultiLanguage(
                    name = query,
                    category = category?.name,
                    language = language.toEntity()
                )
            } else {
                queries.countPagedItemsFiltered(
                    name = query,
                    category = category?.name,
                    language = language.toEntity()
                )
            },
            transacter = queries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                if (isMultiLanguage) {
                    queries.findItemsPagedFilteredMultiLanguage(
                        name = query,
                        category = category?.name,
                        language = language.toEntity(),
                        limit = limit,
                        offset = offset
                    )
                } else {
                    queries.findItemsPagedFiltered(
                        name = query,
                        category = category?.name,
                        language = language.toEntity(),
                        limit = limit,
                        offset = offset
                    )
                }
            }
        )
    }
}

private fun Language.toEntity(): LanguageEntity = when (this) {
    Language.ENGLISH -> LanguageEntity.ENGLISH
    Language.POLISH -> LanguageEntity.POLISH
    Language.GERMAN -> LanguageEntity.GERMAN
    Language.FRENCH -> LanguageEntity.FRENCH
    Language.RUSSIAN -> LanguageEntity.RUSSIAN
    Language.PORTUGUESE -> LanguageEntity.PORTUGUESE
    Language.SPANISH -> LanguageEntity.SPANISH
    Language.UKRAINIAN -> LanguageEntity.UKRAINIAN
}
