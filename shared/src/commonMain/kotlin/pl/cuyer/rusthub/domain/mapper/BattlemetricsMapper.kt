package pl.cuyer.rusthub.domain.mapper

import domain.models.server.battlemetrics.BattlemetricsServerContent
import domain.models.server.battlemetrics.RustWipe
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.ServerInfo
import kotlin.time.Duration
import kotlin.time.DurationUnit

/** strips the map-ID out of the thumbnail URL */
fun BattlemetricsServerContent.extractMapId(): String? =
    attributes.details?.rustMaps?.thumbnailUrl
        ?.substringBefore("/thumbnail")
        ?.substringBefore("/Thumbnail")
        ?.substringAfterLast("/")

/** core mapping of all fields _except_ mapImage/icon */
fun BattlemetricsServerContent.toServerInfo(): ServerInfo =
    ServerInfo(
        id = id.toLongOrNull(),
        name = attributes.name,
        wipe = attributes.details?.rustLastWipe?.let(Instant::parse),
        ranking = attributes.rank?.toDouble(),
        modded = attributes.details?.rustType?.contains("modded"),
        playerCount = attributes.players?.toLong(),
        serverCapacity = attributes.maxPlayers?.toLong(),
        mapName = attributes.details?.map?.substringBefore(" ")?.uppercase()?.let {
            try {
                Maps.valueOf(it)
            } catch (e: Exception) {
                Maps.CUSTOM
            }
        },
        cycle = attributes.details?.rustWipes?.let { calculateCycle(it) },
        serverFlag = attributes.country?.let { Flag.valueOf(it) },
        region = attributes.details?.rustSettings?.timezone?.substringBefore("/")?.uppercase()
            ?.let {
                try {
                    Region.valueOf(it)
                } catch (e: Exception) {
                    null
                }
            },
        maxGroup = attributes.details?.rustSettings?.groupLimit?.toLong(),
        difficulty = attributes.details?.rustGamemode?.uppercase()?.let {
            try {
                Difficulty.valueOf(it)
            } catch (e: Exception) {
                null
            }
        },
        wipeSchedule = attributes.details?.rustSettings?.wipes?.let { WipeSchedule.from(it) },
        isOfficial = attributes.details?.official,
        serverIp = ipPort(attributes.ip ?: "", attributes.port?.toString() ?: ""),
        description = attributes.details?.rustDescription,
        mapId = this.extractMapId()
    )

private fun calculateCycle(wipes: List<RustWipe>): Double? {
    val instants = wipes
        .map { Instant.parse(it.timestamp) }
        .sorted()

    if (instants.size < 2) return null

    val intervals: List<Duration> = instants
        .zipWithNext { earlier, later -> later - earlier }

    val avgInterval = intervals
        .reduce { sum, d -> sum + d } / intervals.size

    return avgInterval.toDouble(DurationUnit.DAYS)
}

private fun ipPort(ip: String, port: String): String = "$ip:$port"