package pl.cuyer.rusthub.data.local.server

import app.cash.paging.PagingSource
import app.cash.sqldelight.paging3.QueryPagingSource
import database.Server
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.ServerDataSource

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
                    mapName = info.mapName,
                    cycle = info.cycle,
                    serverFlag = info.serverFlag,
                    region = info.region,
                    maxGroup = info.maxGroup,
                    difficulty = info.difficulty,
                    wipeSchedule = info.wipeSchedule,
                    isOfficial = info.isOfficial == true,
                    ip = info.serverIp,
                    description = info.description
                )
            }
        }
    }

    override fun getServersPagingSource(query: ServerQuery): PagingSource<Int, Server> {
        val countQuery = with(query) {
            queries.countPagedServers(
                name = name,
                wipe = wipe?.toString(),
                ranking = ranking,
                modded = modded,
                playerCount = playerCount,
                mapName = map,
                serverFlag = flag,
                region = region,
                maxGroup = groupLimit,
                difficulty = difficulty,
                wipeSchedule = wipeSchedule,
                isOfficial = if (official == true) 1 else 0,
                favourite = if (favourite == true) 1 else 0
            )
        }
        val pagingSource: PagingSource<Int, Server> = QueryPagingSource(
            countQuery = countQuery,
            transacter = queries,
            context = Dispatchers.IO,
            queryProvider = { limit: Long, offset: Long ->
                with(query) {
                    queries.findServersPaged(
                        name = name,
                        wipe = wipe?.toString(),
                        ranking = ranking,
                        modded = modded,
                        playerCount = playerCount,
                        mapName = map,
                        serverFlag = flag,
                        region = region,
                        maxGroup = groupLimit,
                        difficulty = difficulty,
                        wipeSchedule = wipeSchedule,
                        isOfficial = if (official == true) 1 else 0,
                        favourite = if (favourite == true) 1 else 0,
                        limit = limit,
                        offset = offset
                    )
                }
            }
        )
        return pagingSource
    }
}