@file:OptIn(ExperimentalTime::class)

package pl.cuyer.rusthub.presentation.model

import kotlinx.serialization.Serializable
import androidx.compose.runtime.Immutable
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.util.StringProvider
import kotlin.time.ExperimentalTime

@Serializable
@Immutable
data class FilterUi(
    val lists: List<FilterDropdownOption> = listOf(),
    val checkboxes: List<FilterCheckboxOption> = listOf(),
    val ranges: List<FilterRangeOption> = listOf(),
    val filter: ServerFilter = ServerFilter.ALL
)

fun FilterUi.toDomain(stringProvider: StringProvider): ServerQuery {
    val selectedMap = lists.getOrNull(0)?.let { it.options.getOrNull(it.selectedIndex ?: -1) }
    val selectedFlag = lists.getOrNull(1)?.let { it.options.getOrNull(it.selectedIndex ?: -1) }
    val selectedRegion = lists.getOrNull(2)?.let { it.options.getOrNull(it.selectedIndex ?: -1) }
    val selectedDifficulty = lists.getOrNull(3)?.let { it.options.getOrNull(it.selectedIndex ?: -1) }
    val selectedWipeSchedule = lists.getOrNull(4)?.let { it.options.getOrNull(it.selectedIndex ?: -1) }
    val selectedOrder = lists.getOrNull(5)?.let { it.options.getOrNull(it.selectedIndex ?: -1) }

    val official = checkboxes.getOrNull(0)?.isChecked
    val modded = checkboxes.getOrNull(1)?.isChecked

    val playerCountRaw = ranges.getOrNull(0)?.value?.toLong()
    val playerCount = if (playerCountRaw == 0L) null else playerCountRaw
    val groupLimitRaw = ranges.getOrNull(1)?.value?.toLong()
    val groupLimit = if (groupLimitRaw == 0L) null else groupLimitRaw
    val rankingRaw = ranges.getOrNull(2)?.value?.toLong()
    val ranking = if (rankingRaw == 0L) null else rankingRaw

    return ServerQuery(
        map = selectedMap?.let { Maps.fromDisplayName(it) },
        flag = selectedFlag?.let { Flag.fromDisplayName(it) },
        region = selectedRegion?.let { Region.fromDisplayName(it, stringProvider) },
        difficulty = selectedDifficulty?.let { Difficulty.fromDisplayName(it) },
        wipeSchedule = selectedWipeSchedule?.let { WipeSchedule.fromDisplayName(it, stringProvider) },
        order = selectedOrder?.let { Order.fromDisplayName(it, stringProvider) } ?: Order.WIPE,
        official = official,
        modded = modded,
        playerCount = playerCount,
        groupLimit = groupLimit,
        ranking = ranking,
        filter = filter
    )
}

fun FilterUi.activeFiltersCount(): Int {
    val dropdownCount = lists.count { it.selectedIndex != null  && it.selectedIndex != -1}
    val checkboxCount = checkboxes.count { it.isChecked }
    val rangeCount = ranges.count { it.value != 0 && it.value != null }
    return dropdownCount + checkboxCount + rangeCount
}
