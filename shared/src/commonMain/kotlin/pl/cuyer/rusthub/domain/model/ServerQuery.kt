package pl.cuyer.rusthub.domain.model

import kotlinx.datetime.Instant
import pl.cuyer.rusthub.domain.model.SortOrder
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.WipeSchedule

data class ServerQuery(
    val name: String? = null,
    val wipe: Instant? = null,
    val rating: Double? = null,
    val modded: Boolean? = null,
    val playerCount: Long? = null,
    val serverCapacity: Long? = null,
    val mapName: Maps? = null,
    val serverFlag: Flag? = null,
    val region: Region? = null,
    val maxGroup: Long? = null,
    val difficulty: Difficulty? = null,
    val wipeSchedule: WipeSchedule? = null,
    val isOfficial: Boolean? = null,
    val serverIp: String? = null,
    val page: Long? = null,
    val size: Long? = null,
    val order: SortOrder = SortOrder.DESC
)