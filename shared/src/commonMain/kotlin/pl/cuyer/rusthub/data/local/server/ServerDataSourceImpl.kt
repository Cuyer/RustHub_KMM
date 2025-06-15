package pl.cuyer.rusthub.data.local.server

import app.cash.paging.PagingSource
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.SortOrder
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.battlemetrics.ServerPagingSource

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
                    description = info.description,
                    mapId = info.mapId
                )
            }
        }
    }

    override fun getPagedServers(
        query: ServerQuery,
        limit: Long,
        offset: Long
    ): List<ServerInfo> = with(query) {
        queries.findServersPaged(
            name = name,
            wipe = wipe?.toString(),
            ranking = ranking,
            modded = if (modded == true) 1 else 0,
            playerCount = playerCount,
            serverCapacity = serverCapacity,
            mapName = mapName,
            serverFlag = serverFlag,
            region = region,
            maxGroup = maxGroup,
            difficulty = difficulty,
            wipeSchedule = wipeSchedule,
            isOfficial = if (isOfficial == true) 1 else 0,
            serverIp = serverIp,
            favourite = if (favourite == true) 1 else 0,
            orderDesc = if (order == SortOrder.DESC) 1 else 0,
            limit = limit,
            offset = offset
        ).executeAsList().map { it.toServerInfo() }
    }

    override fun updateMap(id: Long, mapImage: String) {
        queries.updateMap(id = id, map_image = mapImage)
    }

    override fun updateFavourite(id: Long, favourite: Boolean) {
        queries.updateFavourite(id = id, favourite = if (favourite == true) 1 else 0)
    }

    override fun findKey(key: String): String? {
        return queries.selectRemoteKey(key).executeAsOneOrNull()?.next_url
    }

    override fun insertOrReplaceRemoteKey(id: String, nextKey: String?) {
        queries.insertOrReplaceRemoteKey(id, nextKey, null)
    }

    override fun clearNotFavouriteServers() {
        queries.clearNotFavouriteServers()
    }

    override fun clearRemoteKeys() {
        queries.clearRemoteKeys()
    }

    override fun getServersPagingSource(query: ServerQuery): PagingSource<Int, ServerInfo> {
        return ServerPagingSource(this, query)
    }
}