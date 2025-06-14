package pl.cuyer.rusthub.data.local.server

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.SortOrder
import pl.cuyer.rusthub.domain.repository.ServerDataSource

class ServerDataSourceImpl(
    private val dbQueries: Queries
): ServerDataSource {

    override fun upsertServers(
        servers: List<ServerInfo>
    ) {
        dbQueries.queries.transaction {
            servers.forEach { info ->
                dbQueries.queries.upsertServers(
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
                    mapImage = info.mapImage,
                    description = info.description
                )
            }
        }
    }

    override fun findServers(query: ServerQuery): Flow<List<ServerInfo>> = with(query) {
        val items = dbQueries.queries.findServers(
            name, wipe?.toString(), rating, modded, playerCount, serverCapacity,
            mapName, serverFlag, region, maxGroup,
            difficulty, wipeSchedule, if (isOfficial == true) 1 else 0, serverIp,
            orderDesc = if (order == SortOrder.DESC) 1 else 0
        )

        return items
    }

}