package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.Instant

data class Filters(
    val wipe: Instant?,
    val ranking: Double?,
    val modded: Boolean?,
    val playerCount: Int?,
    val mapName: String?,
    val serverFlag: String?,
    val region: String?,
    val groupLimit: Int?,
    val difficulty: String?,
    val wipeSchedule: String?,
)
