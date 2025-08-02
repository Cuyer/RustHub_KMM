package pl.cuyer.rusthub.data.local.filtersOptions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toDomain
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.util.CrashReporter

class FiltersOptionsDataSourceImpl(
    db: RustHubDatabase
) : Queries(db), FiltersOptionsDataSource {

    override suspend fun upsertFiltersOptions(filtersOptions: FiltersOptions) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.transaction {
                    queries.upsertFiltersOptions(
                        id = DEFAULT_KEY,
                        max_ranking = filtersOptions.maxRanking.toLong(),
                        max_player_count = filtersOptions.maxPlayerCount.toLong(),
                        max_group_limit = filtersOptions.maxGroupLimit.toLong()
                    )

                    queries.clearFiltersFlags()
                    filtersOptions.flags.forEach { flag ->
                        queries.upsertFiltersFlag(
                            id = flag.name,
                            label = flag.name
                        )
                    }

                    queries.clearFiltersMaps()
                    filtersOptions.maps.forEach { map ->
                        queries.upsertFiltersMap(
                            id = map.name,
                            label = map.name
                        )
                    }

                    queries.clearFiltersRegions()
                    filtersOptions.regions.forEach { region ->
                        queries.upsertFiltersRegion(
                            id = region.name,
                            label = region.name
                        )
                    }

                    queries.clearFiltersDifficulty()
                    filtersOptions.difficulty.forEach { diff ->
                        queries.upsertFiltersDifficulty(
                            id = diff.name,
                            label = diff.name
                        )
                    }

                    queries.clearFiltersWipeSchedules()
                    filtersOptions.wipeSchedules.forEach { wipe ->
                        queries.upsertFiltersWipeSchedule(
                            id = wipe.name,
                            label = wipe.name
                        )
                    }
                }
            }
        }
    }

    override fun getFiltersOptions(): Flow<FiltersOptions> = flow {
        val flags = safeQuery(emptyList()) {
            queries.getFiltersFlags()
                .executeAsList()
                .map { it.toDomain() }
        }

        val maps = safeQuery(emptyList()) {
            queries.getFiltersMaps()
                .executeAsList()
                .map { it.toDomain() }
        }

        val regions = safeQuery(emptyList()) {
            queries.getFiltersRegions()
                .executeAsList()
                .map { it.toDomain() }
        }

        val difficulty = safeQuery(emptyList()) {
            queries.getFiltersDifficulty()
                .executeAsList()
                .map { it.toDomain() }
        }

        val wipe = safeQuery(emptyList()) {
            queries.getFiltersWipeSchedules()
                .executeAsList()
                .map { it.toDomain() }
        }

        val rawOptions = safeQuery(null) { queries.getFiltersOptions(DEFAULT_KEY).executeAsOneOrNull() }

        emit(
            rawOptions.toDomain(
                flags = flags,
                maps = maps,
                regions = regions,
                difficulty = difficulty,
                wipeSchedules = wipe
            )
        )
    }.catch { e ->
        CrashReporter.recordException(e)
        throw e
    }.flowOn(Dispatchers.IO)

    override suspend fun clearFiltersOptions() {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.transaction {
                    queries.clearFiltersOptions()
                    queries.clearFiltersFlags()
                    queries.clearFiltersMaps()
                    queries.clearFiltersRegions()
                    queries.clearFiltersDifficulty()
                    queries.clearFiltersWipeSchedules()
                }
            }
        }
    }
}