package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.ServerFilter

import pl.cuyer.rusthub.SharedRes
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
            Triple(
                stringProvider.get(SharedRes.strings.map),
                maps,
                maps.indexOf(this?.map?.displayName ?: "")
            ),
            Triple(
                stringProvider.get(SharedRes.strings.country),
                flags,
                flags.indexOf(this?.flag?.displayName ?: "")
            ),
            Triple(
                stringProvider.get(SharedRes.strings.region),
                regions,
                regions.indexOf(this?.region?.displayName ?: "")
            ),
            Triple(
                stringProvider.get(SharedRes.strings.difficulty),
                difficulties,
                difficulties.indexOf(this?.difficulty?.displayName ?: "")
            ),
            Triple(
                stringProvider.get(SharedRes.strings.wipe_schedule),
                schedules,
                schedules.indexOf(this?.wipeSchedule?.displayName ?: "")
            ),
            Triple(
                stringProvider.get(SharedRes.strings.order),
                order,
                order.indexOf(this?.order?.displayName(stringProvider))
            )
        ),
        checkboxes = listOf(
            stringProvider.get(SharedRes.strings.official) to (this?.official == true),
            stringProvider.get(SharedRes.strings.modded) to (this?.modded == true)
        ),
        ranges = listOf(
            Triple(
                stringProvider.get(SharedRes.strings.max_player_count),
                playerCount,
                (this?.playerCount)?.toInt()
            ),
            Triple(
                stringProvider.get(SharedRes.strings.group_limit),
                groupLimit,
                (this?.groupLimit)?.toInt()
            ),
            Triple(
                stringProvider.get(SharedRes.strings.max_ranking),
                ranking,
                (this?.ranking)?.toInt()
            )
        ),
        filter = this?.filter ?: ServerFilter.ALL
    )
}
