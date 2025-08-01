package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.Instant
import androidx.compose.runtime.Immutable

@Immutable
data class ServerInfo(
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
    val pve: Boolean? = null,
    val website: String? = null,
    val isPremium: Boolean? = null,
    val mapUrl: String? = null,
    val headerImage: String? = null,
    val isFavorite: Boolean? = null,
    val isSubscribed: Boolean? = null,
    val nextWipe: Instant? = null,
    val nextMapWipe: Instant? = null
)