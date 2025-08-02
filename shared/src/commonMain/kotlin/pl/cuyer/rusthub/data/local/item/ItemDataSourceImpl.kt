package pl.cuyer.rusthub.data.local.item

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.paging3.QueryPagingSource
import database.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toRustItem
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.Crafting
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.LootContent
import pl.cuyer.rusthub.domain.model.Looting
import pl.cuyer.rusthub.domain.model.Raiding
import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.WhereToFind
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.util.CrashReporter

class ItemDataSourceImpl(
    db: RustHubDatabase,
    private val json: Json
) : ItemDataSource, Queries(db) {

    override suspend fun upsertItems(items: List<RustItem>) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.transaction {
                    items.forEach { item ->
                        item.id?.let {
                            queries.upsertItem(
                                id = item.id,
                                slug = item.slug,
                                url = item.url,
                                name = item.name,
                                description = item.description,
                                image = item.image,
                                stackSize = item.stackSize?.toLong(),
                                health = item.health?.toLong(),
                                categories = item.categories?.joinToString(",") { it.name },
                                shortName = item.shortName,
                                iconUrl = item.iconUrl,
                                language = item.language?.name ?: Language.ENGLISH.name,
                                looting = item.looting?.let {
                                    json.encodeToString(ListSerializer(Looting.serializer()), it)
                                },
                                lootContents = item.lootContents?.let {
                                    json.encodeToString(ListSerializer(LootContent.serializer()), it)
                                },
                                whereToFind = item.whereToFind?.let {
                                    json.encodeToString(ListSerializer(WhereToFind.serializer()), it)
                                },
                                crafting = item.crafting?.let { json.encodeToString(it) },
                                recycling = item.recycling?.let { json.encodeToString(it) },
                                raiding = item.raiding?.let {
                                    json.encodeToString(ListSerializer(Raiding.serializer()), it)
                                }
                            )
                        } ?: throw IllegalArgumentException("ID cannot be null")
                    }
                }
            }
        }
    }

    /**
     * Checks if the database is empty for a given language.
     *
     * The `updatedLanguage` variable is used to handle a specific limitation where the Polish language
     * is not supported in the API. If the provided language is Polish, it is replaced with English
     * to ensure the query executes correctly. This workaround ensures compatibility with the API
     * while maintaining functionality for other languages.
     *
     * @param language The `Language` to check for.
     * @return `true` if the database is empty for the given language, `false` otherwise.
     */
    override suspend fun isEmpty(language: Language): Boolean {
        return withContext(Dispatchers.IO) {
            safeQuery(true) {
                val updatedLanguage = if (language == Language.POLISH) {
                    Language.ENGLISH
                } else {
                    language
                }
                queries.countItems(language = updatedLanguage.name).executeAsOne() == 0L
            }
        }
    }

    override fun getItemsPagingSource(
        name: String?,
        category: ItemCategory?,
        language: Language,
    ): PagingSource<Int, ItemEntity> {
        return QueryPagingSource(
            countQuery = queries.countPagedItemsFiltered(
                name = name ?: "",
                category = category?.name,
                language = language.name
            ),
            transacter = queries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                queries.findItemsPagedFiltered(
                    name = name ?: "",
                    category = category?.name,
                    language = language.name,
                    limit = limit,
                    offset = offset
                )
            }
        )
    }

    override fun getItemById(id: Long, language: Language): Flow<RustItem?> {
        return queries.getItemById(id = id, language = language.name)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toRustItem(json) }
            .catch { e ->
                CrashReporter.recordException(e)
                throw e
            }
    }
}
