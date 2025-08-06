package pl.cuyer.rusthub.data.local.monument

import androidx.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.paging3.QueryPagingSource
import database.MonumentEntity
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
import pl.cuyer.rusthub.data.local.mapper.toEntity
import pl.cuyer.rusthub.data.local.mapper.toMonument
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentPuzzle
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.model.UsableEntity
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource
import pl.cuyer.rusthub.util.CrashReporter

class MonumentDataSourceImpl(
    db: RustHubDatabase,
    private val json: Json
) : MonumentDataSource, Queries(db) {
    override suspend fun upsertMonuments(monuments: List<Monument>) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.transaction {
                    monuments.forEach { monument ->
                        queries.upsertMonument(
                            slug = monument.slug ?: "",
                            name = monument.name,
                            attributes = monument.attributes?.let { json.encodeToString(it) },
                            spawns = monument.spawns?.let { json.encodeToString(it) },
                            usableEntities = monument.usableEntities?.let {
                                json.encodeToString(ListSerializer(UsableEntity.serializer()), it)
                            },
                            mining = monument.mining?.let { json.encodeToString(it) },
                            puzzles = monument.puzzles?.let {
                                json.encodeToString(ListSerializer(MonumentPuzzle.serializer()), it)
                            },
                            language = monument.language.toEntity()
                        )
                    }
                }
            }
        }
    }

    override suspend fun isEmpty(language: Language): Boolean {
        return withContext(Dispatchers.IO) {
            safeQuery(true) {
                queries.countMonuments(language = language.toEntity()).executeAsOne() == 0L
            }
        }
    }

    override fun getMonumentsPagingSource(
        name: String?,
        type: MonumentType?,
        language: Language,
    ): PagingSource<Int, MonumentEntity> {
        return QueryPagingSource(
            countQuery = queries.countPagedMonumentsFiltered(
                name = name ?: "",
                type = type?.toDbValue(),
                language = language.toEntity()
            ),
            transacter = queries,
            context = Dispatchers.IO,
            queryProvider = { limit, offset ->
                queries.findMonumentsPagedFiltered(
                    name = name ?: "",
                    type = type?.toDbValue(),
                    language = language.toEntity(),
                    limit = limit,
                    offset = offset
                )
            }
        )
    }

    override fun getMonumentBySlug(slug: String, language: Language): Flow<Monument?> {
        return queries.getMonumentBySlug(slug = slug, language = language.toEntity())
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toMonument(json) }
            .catch { e ->
                CrashReporter.recordException(e)
                throw e
            }
    }

    private fun MonumentType.toDbValue(): String = when (this) {
        MonumentType.SMALL -> "Small"
        MonumentType.SAFE_ZONES -> "Safe Zones"
        MonumentType.OCEANSIDE -> "Oceanside"
        MonumentType.MEDIUM -> "Medium"
        MonumentType.ROADSIDE -> "Roadside"
        MonumentType.OFFSHORE -> "Offshore"
        MonumentType.LARGE -> "Large"
    }

}
