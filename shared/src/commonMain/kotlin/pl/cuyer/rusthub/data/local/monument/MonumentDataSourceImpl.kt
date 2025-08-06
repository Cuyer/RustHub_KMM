package pl.cuyer.rusthub.data.local.monument

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentPuzzle
import pl.cuyer.rusthub.domain.model.UsableEntity
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource

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
                            language = monument.language ?: Language.ENGLISH.name
                        )
                    }
                }
            }
        }
    }

    override suspend fun isEmpty(language: Language): Boolean {
        return withContext(Dispatchers.IO) {
            safeQuery(true) {
                queries.countMonuments(language = language.name).executeAsOne() == 0L
            }
        }
    }
}
