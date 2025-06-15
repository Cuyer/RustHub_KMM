package pl.cuyer.rusthub.domain.mapper

import database.Server
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.domain.model.ServerInfo

fun Server.toServerInfo(): ServerInfo {
    return ServerInfo(
        id = id,
        name = name,
        wipe = wipe?.let { Instant.parse(it) },
        ranking = ranking,
        modded = modded == 1L ,
        playerCount = player_count,
        serverCapacity = capacity,
        mapName = map_name,
        cycle = cycle,
        serverFlag = server_flag,
        region = region,
        maxGroup = max_group,
        difficulty = difficulty,
        wipeSchedule = wipe_schedule,
        isOfficial = is_official == 1L,
        serverIp = ip,
        mapImage = map_image,
        description = description
    )
}
