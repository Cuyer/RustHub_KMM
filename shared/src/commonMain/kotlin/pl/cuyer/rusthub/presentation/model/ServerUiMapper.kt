@file:OptIn(ExperimentalTime::class)

package pl.cuyer.rusthub.presentation.model

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.util.formatLocalDateTime
import kotlin.time.Clock
import kotlin.time.Instant
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import kotlin.time.ExperimentalTime

fun ServerInfo.toUi(stringProvider: StringProvider): ServerInfoUi {
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
        lastWipe = wipe?.let { formatLastWipe(it, stringProvider) },
        nextWipe = nextWipe?.let { formatNextWipe(it, stringProvider) },
        nextMapWipe = nextMapWipe?.let { formatNextWipe(it, stringProvider) },
        pve = pve,
        website = website,
        isPremium = isPremium,
        mapUrl = mapUrl,
        headerImage = headerImage,
        isFavorite = isFavorite,
        isSubscribed = isSubscribed
    )

}

fun formatLastWipe(
    wipeInstant: Instant,
    stringProvider: StringProvider,
): String {
    val now = Clock.System.now()
    val duration = now - wipeInstant
    val localDateTime = wipeInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = formatLocalDateTime(localDateTime)

    val timeAgo = when {
        duration.inWholeDays >= 1 ->
            stringProvider.get(SharedRes.strings.days_ago, duration.inWholeDays)
        duration.inWholeHours >= 1 ->
            stringProvider.get(SharedRes.strings.hours_ago, duration.inWholeHours)
        else ->
            stringProvider.get(SharedRes.strings.minutes_ago, duration.inWholeMinutes)
    }

    return "$formattedDate ($timeAgo)"
}

fun formatNextWipe(
    wipeInstant: Instant,
    stringProvider: StringProvider,
): String {
    val now = Clock.System.now()
    val duration = wipeInstant - now
    val localDateTime = wipeInstant.toLocalDateTime(TimeZone.currentSystemDefault())
    val formattedDate = formatLocalDateTime(localDateTime)

    val inTime = when {
        duration.inWholeDays >= 1 ->
            stringProvider.get(SharedRes.strings.in_days, duration.inWholeDays)
        duration.inWholeHours >= 1 ->
            stringProvider.get(SharedRes.strings.in_hours, duration.inWholeHours)
        duration.inWholeMinutes >= 0 ->
            stringProvider.get(SharedRes.strings.in_minutes, duration.inWholeMinutes)
        else -> stringProvider.get(SharedRes.strings.soon)
    }

    return "$formattedDate ($inTime)"
}
