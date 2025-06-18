package pl.cuyer.rusthub.data.local.filter

import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toEntity
import pl.cuyer.rusthub.data.local.mapper.toServerQuery
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.FiltersDataSource

class FiltersDataSourceImpl(
    db: RustHubDatabase
) : FiltersDataSource, Queries(db) {

    override fun getFilters(): ServerQuery? = queries.getFilters(DEFAULT_KEY).executeAsOneOrNull()?.toServerQuery()

    override fun upsertFilters(filters: ServerQuery) {
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
            is_official = if (filters.official == true) 1 else 0,
            sort_order = filters.order.toEntity()
        )
    }

    override fun clearFilters() {
        queries.clearFilters()
    }

}