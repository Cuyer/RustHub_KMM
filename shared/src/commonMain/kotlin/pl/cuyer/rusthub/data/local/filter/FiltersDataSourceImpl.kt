package pl.cuyer.rusthub.data.local.filter

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toEntity
import pl.cuyer.rusthub.data.local.mapper.toServerQuery
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource

class FiltersDataSourceImpl(
    db: RustHubDatabase
) : FiltersDataSource, Queries(db) {

    override fun getFilters(): Flow<ServerQuery?> {
        return queries
            .getFilters(DEFAULT_KEY)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toServerQuery() }
    }

    override suspend fun upsertFilters(filters: ServerQuery) {
        withContext(Dispatchers.IO) {
            queries.upsertFilters(
                id = DEFAULT_KEY,
                wipe = filters.wipe?.toString(),
                ranking = filters.ranking,
                player_count = filters.playerCount,
                map_name = filters.map.toEntity(),
                server_flag = filters.flag.toEntity(),
                region = filters.region.toEntity(),
                group_limit = filters.groupLimit,
                difficulty = filters.difficulty.toEntity(),
                wipe_schedule = filters.wipeSchedule.toEntity(),
                is_official = if (filters.official == true) 1 else null,
                modded = if (filters.modded == true) 1 else null,
                sort_order = filters.order.toEntity()
            )
        }
    }

    override suspend fun clearFilters() {
        withContext(Dispatchers.IO) {
            queries.clearFilters()
        }
    }
}