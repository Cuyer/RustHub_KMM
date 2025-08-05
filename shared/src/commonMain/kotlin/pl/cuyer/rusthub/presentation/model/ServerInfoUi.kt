@file:OptIn(ExperimentalTime::class)

package pl.cuyer.rusthub.presentation.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable
import kotlin.time.Clock
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.WipeType
import kotlin.time.Instant
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider
import kotlin.getValue
import kotlin.ranges.contains
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Serializable
@Immutable
data class ServerInfoUi(
    val id: Long? = null,
    val name: String? = null,
    val wipe: Instant? = null,
    val ranking: Long? = null,
    val modded: Boolean? = null,
    val playerCount: Long? = null,
    val serverCapacity: Long? = null,
    val mapName: Maps? = null,
    val cycle: Double? = null,
    val serverFlag: Flag? = null,
    val region: Region? = null,
    val maxGroup: Long? = null,
    val difficulty: Difficulty? = null,
    val wipeSchedule: WipeSchedule? = null,
    val isOfficial: Boolean? = null,
    val serverIp: String? = null,
    val mapImage: String? = null,
    val description: String? = null,
    val serverStatus: ServerStatus? = null,
    val wipeType: WipeType? = null,
    val blueprints: Boolean? = null,
    val kits: Boolean? = null,
    val decay: Float? = null,
    val upkeep: Float? = null,
    val rates: Int? = null,
    val seed: Long? = null,
    val mapSize: Int? = null,
    val monuments: Int? = null,
    val averageFps: Long? = null,
    val lastWipe: String? = null,
    val nextWipe: String? = null,
    val nextMapWipe: String? = null,
    val pve: Boolean? = null,
    val website: String? = null,
    val isPremium: Boolean? = null,
    val mapUrl: String? = null,
    val headerImage: String? = null,
    val isFavorite: Boolean? = null,
    val isSubscribed: Boolean? = null
)

fun ServerInfoUi.createLabels(stringProvider: StringProvider): List<String> {
    val labels = mutableListOf<String>()

    this.wipeSchedule?.let {
        val text = when (it) {
            WipeSchedule.WEEKLY -> stringProvider.get(SharedRes.strings.weekly)
            WipeSchedule.BIWEEKLY -> stringProvider.get(SharedRes.strings.biweekly)
            WipeSchedule.MONTHLY -> stringProvider.get(SharedRes.strings.monthly)
        }
        labels.add(text)
    }
    this.difficulty?.let {
        labels.add(it.name)
    }
    if (this.isOfficial == true) {
        labels.add(stringProvider.get(SharedRes.strings.official))
    }

    this.wipeType?.let {
        if (it != WipeType.UNKNOWN) {
            labels.add(it.name + " " + stringProvider.get(SharedRes.strings.wipe))
        }
    }

    return labels
}

fun ServerInfoUi.createDetails(stringProvider: StringProvider): Map<String, String> {
    val details = mutableMapOf<String, String>()
    this.wipe?.let { wipeInstant: Instant ->
        val now = Clock.System.now()
        val duration = now - wipeInstant
        val minutesAgo = duration.inWholeMinutes

        val parsedTimeAgo = when (minutesAgo) {
            in 0..60 ->
                stringProvider.get(SharedRes.strings.minutes_ago, minutesAgo)

            in 61..1440 ->
                stringProvider.get(SharedRes.strings.hours_ago, minutesAgo / 60)

            in 1441..10080 ->
                stringProvider.get(SharedRes.strings.days_ago, minutesAgo / 1440)

            else -> stringProvider.get(SharedRes.strings.weeks_ago, minutesAgo / 10080)
        }

        details[stringProvider.get(SharedRes.strings.wipe).trim()] = parsedTimeAgo
    }

    this.ranking?.let {
        details[stringProvider.get(SharedRes.strings.ranking)] = it.toInt().toString()
    }
    this.cycle?.let {
        val formatted = it.asTwoDecimalString()
        val cycleValue = "~ $formatted ${stringProvider.get(SharedRes.strings.days).trim()}"
        details[stringProvider.get(SharedRes.strings.cycle)] = cycleValue
    }

    this.serverCapacity?.let {
        details[stringProvider.get(SharedRes.strings.players)] =
            "${this.playerCount ?: 0}/$it"
    }
    this.mapName?.let {
        details[stringProvider.get(SharedRes.strings.map)] = it.name
    }
    this.modded?.let {
        details[stringProvider.get(SharedRes.strings.modded)] =
            if (it) stringProvider.get(SharedRes.strings.yes)
            else stringProvider.get(SharedRes.strings.no)
    }

    return details
}


fun Double.asTwoDecimalString(): String {
    return (kotlin.math.round(this * 100) / 100).toString()
        .let {
            if (!it.contains(".")) "$it.00"
            else it.padEnd(it.indexOf('.') + 3, '0')
        }
}