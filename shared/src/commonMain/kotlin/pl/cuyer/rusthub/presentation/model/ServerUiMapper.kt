package pl.cuyer.rusthub.presentation.model

import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
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
        serverStatus = serverStatus,
        wipeType = wipeType,
        blueprints = blueprints,
        kits = kits,
        decay = decay,
        upkeep = upkeep,
        rates = rates,
        seed = seed,
        mapSize = mapSize,
        monuments = monuments,
        averageFps = averageFps,
        lastWipe = wipe?.let { formatLastWipe(it) },
        pve = pve,
        website = website,
        isPremium = isPremium
    )

}

fun formatLastWipe(wipeInstant: Instant): String {
    val now = Clock.System.now()
    val duration = now - wipeInstant
    val localDate = wipeInstant.toLocalDateTime(TimeZone.currentSystemDefault()).date

    val timeAgo = when {
        duration.inWholeDays >= 1 -> "${duration.inWholeDays} days ago"
        duration.inWholeHours >= 1 -> "${duration.inWholeHours} hours ago"
        else -> "${duration.inWholeMinutes} minutes ago"
    }

    return "$localDate ($timeAgo)"
}
