package pl.cuyer.rusthub.data.local.server

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import database.ServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toEntity
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class ServerDataSourceImpl(
    db: RustHubDatabase
) : ServerDataSource, Queries(db) {

    override fun upsertServers(
        servers: List<ServerInfo>
    ) {
        queries.transaction {
            servers.forEach { info ->
                queries.upsertServers(
                    id = info.id,
                    name = info.name ?: "Undefined",
                    wipe = info.wipe?.toString(),
                    ranking = info.ranking,
                    modded = info.modded == true,
                    playerCount = info.playerCount,
                    capacity = info.serverCapacity,
                    mapName = info.mapName.toEntity(),
                    cycle = info.cycle,
                    serverFlag = info.serverFlag.toEntity(),
                    region = info.region.toEntity(),
                    maxGroup = info.maxGroup,
                    difficulty = info.difficulty.toEntity(),
                    wipeSchedule = info.wipeSchedule.toEntity(),
                    isOfficial = info.isOfficial == true,
                    ip = info.serverIp,
                    description = info.description
                )
            }
        }
    }

    override fun getServersPagingSource(query: ServerQuery?): PagingSource<Int, ServerEntity> {
        val pagingSource: PagingSource<Int, ServerEntity> = QueryPagingSource(
            countQuery = queries.countPagedServersFiltered(
                wipe = query?.wipe?.toString(),
                ranking = query?.ranking,
                modded = query?.modded,
                player_count = query?.playerCount,
                map_name = query?.map?.toEntity(),
                server_flag = query?.flag?.toEntity(),
                region = query?.region?.toEntity(),
                group_limit = query?.groupLimit,
                difficulty = query?.difficulty?.toEntity(),
                wipe_schedule = query?.wipeSchedule?.toEntity(),
                is_official = query?.official
            ),
            transacter = queries,
            context = Dispatchers.IO,
            queryProvider = { limit: Long, offset: Long ->
                queries.findServersPagedFiltered(
                    limit = limit,
                    offset = offset,
                    wipe = query?.wipe?.toString(),
                    ranking = query?.ranking,
                    modded = query?.modded,
                    player_count = query?.playerCount,
                    map_name = query?.map?.toEntity(),
                    server_flag = query?.flag?.toEntity(),
                    region = query?.region?.toEntity(),
                    group_limit = query?.groupLimit,
                    difficulty = query?.difficulty?.toEntity(),
                    wipe_schedule = query?.wipeSchedule?.toEntity(),
                    is_official = query?.official
                )
            }
        )
        return pagingSource
    }

    override fun deleteServers() {
        queries.clearServers()
    }
}