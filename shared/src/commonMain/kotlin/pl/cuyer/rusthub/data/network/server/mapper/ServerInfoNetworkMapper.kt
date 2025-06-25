package pl.cuyer.rusthub.data.network.server.mapper

import pl.cuyer.rusthub.data.network.filtersOptions.mapper.toDomain
import pl.cuyer.rusthub.data.network.server.model.PagedServerInfoDto
import pl.cuyer.rusthub.data.network.server.model.ServerInfoDto
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
        mapName = mapName?.toDomain(),
        cycle = cycle,
        serverFlag = serverFlag?.toDomain(),
        region = region?.toDomain(),
        maxGroup = maxGroup,
        difficulty = difficulty?.toDomain(),
        wipeSchedule = wipeSchedule?.toDomain(),
        isOfficial = isOfficial,
        serverIp = serverIp,
        mapImage = mapImage,
        description = description,
        serverStatus = serverStatus?.toDomain(),
        wipeType = wipeType?.toDomain(),
        blueprints = blueprints,
        kits = kits,
        decay = decay,
        upkeep = upkeep,
        rates = rates,
        seed = seed,
        mapSize = mapSize,
        monuments = monuments,
        averageFps = averageFps,
        pve = pve,
        website = website,
        isPremium = isPremium,
        mapUrl = mapUrl,
        headerImage = headerImage,
        isFavorite = isFavorite,
        isSubscribed = isSubscribed,
        nextWipe = nextWipe,
        nextMapWipe = nextMapWipe
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
