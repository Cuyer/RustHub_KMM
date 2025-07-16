package pl.cuyer.rusthub.presentation.model

import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.util.StringProvider

@Serializable
data class FilterUi(
    val lists: List<Triple<String, List<String>, Int?>> = listOf(),
    val checkboxes: List<Pair<String, Boolean>> = listOf(),
    val ranges: List<Triple<String, Int, Int?>> = listOf(),
    val filter: ServerFilter = ServerFilter.ALL
)

fun FilterUi.toDomain(stringProvider: StringProvider): ServerQuery {
    val selectedMap = lists.getOrNull(0)?.let { it.second.getOrNull(it.third ?: -1) }
    val selectedFlag = lists.getOrNull(1)?.let { it.second.getOrNull(it.third ?: -1) }
    val selectedRegion = lists.getOrNull(2)?.let { it.second.getOrNull(it.third ?: -1) }
    val selectedDifficulty = lists.getOrNull(3)?.let { it.second.getOrNull(it.third ?: -1) }
    val selectedWipeSchedule = lists.getOrNull(4)?.let { it.second.getOrNull(it.third ?: -1) }
    val selectedOrder = lists.getOrNull(5)?.let { it.second.getOrNull(it.third ?: -1) }

    val official = checkboxes.getOrNull(0)?.second
    val modded = checkboxes.getOrNull(1)?.second

    val playerCount = ranges.getOrNull(0)?.third?.toLong()
    val groupLimit = ranges.getOrNull(1)?.third?.toLong()
    val ranking = ranges.getOrNull(2)?.third?.toLong()

    return ServerQuery(
        map = selectedMap?.let { Maps.fromDisplayName(it) },
        flag = selectedFlag?.let { Flag.fromDisplayName(it) },
        region = selectedRegion?.let { Region.fromDisplayName(it) },
        difficulty = selectedDifficulty?.let { Difficulty.fromDisplayName(it) },
        wipeSchedule = selectedWipeSchedule?.let { WipeSchedule.fromDisplayName(it) },
        order = selectedOrder?.let { Order.fromDisplayName(it, stringProvider) } ?: Order.WIPE,
        official = official,
        modded = modded,
        playerCount = playerCount,
        groupLimit = groupLimit,
        ranking = ranking,
        filter = filter
    )
}