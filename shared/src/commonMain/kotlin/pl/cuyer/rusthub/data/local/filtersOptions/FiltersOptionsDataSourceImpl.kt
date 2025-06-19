package pl.cuyer.rusthub.data.local.filtersOptions

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toDomain
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource

class FiltersOptionsDataSourceImpl(
    db: RustHubDatabase
) : Queries(db), FiltersOptionsDataSource {

    override fun upsertFiltersOptions(filtersOptions: FiltersOptions) {
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

    override fun getFiltersOptions(): Flow<FiltersOptions> {
        val flags = queries.getFiltersFlags()
            .executeAsList()
            .map { it.toDomain() }

        val maps = queries.getFiltersMaps()
            .executeAsList()
            .map { it.toDomain() }

        val regions = queries.getFiltersRegions()
            .executeAsList()
            .map { it.toDomain() }

        val difficulty = queries.getFiltersDifficulty()
            .executeAsList()
            .map { it.toDomain() }

        val wipe = queries.getFiltersWipeSchedules()
            .executeAsList()
            .map { it.toDomain() }

        return queries.getFiltersOptions(DEFAULT_KEY)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map {
                it.toDomain(
                    flags = flags,
                    maps = maps,
                    regions = regions,
                    difficulty = difficulty,
                    wipeSchedules = wipe
                )
            }
    }

    override fun clearFiltersOptions() {
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