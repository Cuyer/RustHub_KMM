package pl.cuyer.rusthub.data.network.server.mapper

import pl.cuyer.rusthub.data.network.server.model.dto.PagedServerInfoDto
import pl.cuyer.rusthub.data.network.server.model.dto.ServerInfoDto
import pl.cuyer.rusthub.domain.model.PagedServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo

fun ServerInfoDto.toDomain(): ServerInfo {
    return ServerInfo(
        id = id,
        name = name,
        wipe = wipe,
        ranking = ranking,
        modded = modded,
        playerCount = playerCount,
        serverCapacity = serverCapacity,
        mapName = mapName,
        cycle = cycle,
        serverFlag = serverFlag,
        region = region,
        maxGroup = maxGroup,
        difficulty = difficulty,
        wipeSchedule = wipeSchedule,
        isOfficial = isOfficial,
        serverIp = serverIp,
        mapImage = mapImage,
        description = description,
        mapId = mapId,
    )
}

fun PagedServerInfoDto.toDomain(): PagedServerInfo {
    return PagedServerInfo(
        servers = servers.map { it.toDomain() },
        size = size,
        totalPages = totalPages,
        totalItems = totalItems,
    )
}
