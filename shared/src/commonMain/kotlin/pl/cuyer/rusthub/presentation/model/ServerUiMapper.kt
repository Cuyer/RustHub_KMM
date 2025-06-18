package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.ServerInfo

fun ServerInfo.toUi(): ServerInfoUi {
    return ServerInfoUi(
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
