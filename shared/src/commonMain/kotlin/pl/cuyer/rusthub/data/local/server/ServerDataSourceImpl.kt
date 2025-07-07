package pl.cuyer.rusthub.data.local.server

import app.cash.paging.PagingSource
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import app.cash.sqldelight.paging3.QueryPagingSource
import database.ServerEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toEntity
import pl.cuyer.rusthub.data.local.mapper.toServerInfo
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class ServerDataSourceImpl(
    db: RustHubDatabase
) : ServerDataSource, Queries(db) {

    override suspend fun upsertServers(
        servers: List<ServerInfo>
    ) {
        withContext(Dispatchers.IO) {
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
                        description = info.description,
                        server_status = info.serverStatus.toEntity(),
                        wipe_type = info.wipeType.toEntity(),
                        blueprints = info.blueprints == true,
                        kits = info.kits == true,
                        decay = info.decay?.toDouble(),
                        upkeep = info.upkeep?.toDouble(),
                        rates = info.rates?.toLong(),
                        seed = info.seed?.toLong(),
                        mapSize = info.mapSize?.toLong(),
                        mapImage = info.mapImage,
                        averageFps = info.averageFps?.toLong(),
                        pve = info.pve == true,
                        website = info.website,
                        isPremium = info.isPremium == true,
                        monuments = info.monuments?.toLong(),
                        mapUrl = info.mapUrl,
                        headerImage = info.headerImage,
                        favourite = info.isFavorite == true,
                        subscribed = info.isSubscribed == true,
                        nextWipe = info.nextWipe?.toString(),
                        nextMapWipe = info.nextMapWipe?.toString()
                    )
                }
            }
        }
    }

    override fun getServersPagingSource(
        searchQuery: String?
    ): PagingSource<Int, ServerEntity> {
        val pagingSource: PagingSource<Int, ServerEntity> = QueryPagingSource(
            countQuery = queries.countPagedServersFiltered(
                id = DEFAULT_KEY,
                name = searchQuery ?: ""
            ),
            transacter = queries,
            context = Dispatchers.IO,
            queryProvider = { limit: Long, offset: Long ->
                queries.findServersPagedFiltered(
                    id = DEFAULT_KEY,
                    name = searchQuery ?: "",
                    limit = limit,
                    offset = offset
                )
            }
        )
        return pagingSource
    }

    override fun getServerById(serverId: Long): Flow<ServerInfo?> {
        return queries.getServerById(serverId)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toServerInfo() }
    }

    override suspend fun deleteServers() {
        withContext(Dispatchers.IO) {
            queries.clearServers()
        }
    }

    override suspend fun updateFavourite(serverId: Long, favourite: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching {
                queries.updateFavourite(id = serverId, favourite = favourite)
            }
        }
    }

    override suspend fun updateSubscription(serverId: Long, subscribed: Boolean) {
        withContext(Dispatchers.IO) {
            runCatching {
                queries.updateSubscription(id = serverId, subscribed = subscribed)
            }
        }
    }
}