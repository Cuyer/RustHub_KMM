package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.ServerFilter

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.domain.model.displayName
import pl.cuyer.rusthub.util.StringProvider

fun ServerQuery?.toUi(
    stringProvider: StringProvider,
    maps: List<String> = emptyList(),
    flags: List<String> = emptyList(),
    regions: List<String> = emptyList(),
    difficulties: List<String> = emptyList(),
    schedules: List<String> = emptyList(),
    playerCount: Int,
    groupLimit: Int,
    ranking: Int
): FilterUi {

    val order = Order.entries.map { it.displayName(stringProvider) }

    return FilterUi(
        lists = listOf(
            FilterDropdownOption(
                label = stringProvider.get(SharedRes.strings.map),
                options = maps,
                selectedIndex = maps.indexOf(this?.map?.displayName ?: "").takeIf { it >= 0 }
            ),
            FilterDropdownOption(
                label = stringProvider.get(SharedRes.strings.country),
                options = flags,
                selectedIndex = flags.indexOf(this?.flag?.displayName ?: "").takeIf { it >= 0 }
            ),
            FilterDropdownOption(
                label = stringProvider.get(SharedRes.strings.region),
                options = regions,
                selectedIndex = regions.indexOf(this?.region?.displayName(stringProvider) ?: "").takeIf { it >= 0 }
            ),
            FilterDropdownOption(
                label = stringProvider.get(SharedRes.strings.difficulty),
                options = difficulties,
                selectedIndex = difficulties.indexOf(this?.difficulty?.displayName ?: "").takeIf { it >= 0 }
            ),
            FilterDropdownOption(
                label = stringProvider.get(SharedRes.strings.wipe_schedule),
                options = schedules,
                selectedIndex = schedules.indexOf(this?.wipeSchedule?.displayName(stringProvider) ?: "").takeIf { it >= 0 }
            ),
            FilterDropdownOption(
                label = stringProvider.get(SharedRes.strings.order),
                options = order,
                selectedIndex = order.indexOf(this?.order?.displayName(stringProvider)).takeIf { it >= 0 }
            )
        ),
        checkboxes = listOf(
            FilterCheckboxOption(
                label = stringProvider.get(SharedRes.strings.official),
                isChecked = this?.official == true
            ),
            FilterCheckboxOption(
                label = stringProvider.get(SharedRes.strings.modded),
                isChecked = this?.modded == true
            )
        ),
        ranges = listOf(
            FilterRangeOption(
                label = stringProvider.get(SharedRes.strings.max_player_count),
                max = playerCount,
                value = this?.playerCount?.toInt()
            ),
            FilterRangeOption(
                label = stringProvider.get(SharedRes.strings.group_limit),
                max = groupLimit,
                value = this?.groupLimit?.toInt()
            ),
            FilterRangeOption(
                label = stringProvider.get(SharedRes.strings.max_ranking),
                max = ranking,
                value = this?.ranking?.toInt()
            )
        ),
        filter = this?.filter ?: ServerFilter.ALL
    )
}
