package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.Instant

data class ServerInfo(
    val id: Long? = null,
    val name: String? = null,
    val wipe: Instant? = null,
    val ranking: Double? = null,
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
    val description: String? = null
)