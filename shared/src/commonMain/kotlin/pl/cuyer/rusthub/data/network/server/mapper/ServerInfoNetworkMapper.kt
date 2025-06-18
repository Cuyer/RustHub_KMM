package pl.cuyer.rusthub.data.network.server.mapper

import pl.cuyer.rusthub.data.local.mapper.toDomain
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
        mapName = mapName.toDomain(),
        cycle = cycle,
        serverFlag = serverFlag.toDomain(),
        region = region.toDomain(),
        maxGroup = maxGroup,
        difficulty = difficulty.toDomain(),
        wipeSchedule = wipeSchedule.toDomain(),
        isOfficial = isOfficial,
        serverIp = serverIp,
        mapImage = mapImage,
        description = description
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
