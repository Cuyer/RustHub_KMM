package pl.cuyer.rusthub.domain.model

import kotlin.time.Instant
import androidx.compose.runtime.Immutable
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
@Immutable
data class ServerQuery(
    val wipe: Instant? = null,
    val ranking: Long? = null,
    val modded: Boolean? = null,
    val playerCount: Long? = null,
    val map: Maps? = null,
    val flag: Flag? = null,
    val region: Region? = null,
    val groupLimit: Long? = null,
    val difficulty: Difficulty? = null,
    val wipeSchedule: WipeSchedule? = null,
    val official: Boolean? = null,
    val order: Order = Order.WIPE,
    val filter: ServerFilter = ServerFilter.ALL)