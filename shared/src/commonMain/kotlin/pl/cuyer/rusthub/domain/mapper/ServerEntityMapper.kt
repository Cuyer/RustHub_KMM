package pl.cuyer.rusthub.domain.mapper

import database.ServerEntity
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.model.*
import pl.cuyer.rusthub.domain.model.ServerInfo

fun ServerEntity.toServerInfo(): ServerInfo {
    return ServerInfo(
        id = id,
        name = name,
        wipe = wipe?.let { Instant.parse(it) },
        ranking = ranking,
        modded = modded == 1L ,
        playerCount = player_count,
        serverCapacity = capacity,
        mapName = map_name.toDomain(),
        cycle = cycle,
        serverFlag = server_flag.toDomain(),
        region = region.toDomain(),
        maxGroup = max_group,
        difficulty = difficulty.toDomain(),
        wipeSchedule = wipe_schedule.toDomain(),
        isOfficial = is_official == 1L,
        serverIp = ip,
        mapImage = map_image,
        description = description
    )
}
