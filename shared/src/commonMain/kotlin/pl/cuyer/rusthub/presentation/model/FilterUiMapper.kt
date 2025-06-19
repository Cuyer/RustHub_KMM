package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.displayName

fun ServerQuery?.toUi(
    maps: List<String> = emptyList(),
    flags: List<String> = emptyList(),
    regions: List<String> = emptyList(),
    difficulties: List<String> = emptyList(),
    schedules: List<String> = emptyList(),
    playerCount: Int,
    groupLimit: Int,
    ranking: Int
): FilterUi {

    val order = Order.entries.map { it.displayName }

    return FilterUi(
        lists = listOf(
            Triple("Map", maps, maps.indexOf(this?.map?.displayName ?: "")),
            Triple("Country", flags, flags.indexOf(this?.flag?.displayName ?: "")),
            Triple("Region", regions, regions.indexOf(this?.region?.displayName ?: "")),
            Triple(
                "Difficulty",
                difficulties,
                difficulties.indexOf(this?.difficulty?.displayName ?: "")
            ),
            Triple(
                "Wipe Schedule",
                schedules,
                schedules.indexOf(this?.wipeSchedule?.displayName ?: "")
            ),
            Triple("Order", order, order.indexOf(this?.order?.displayName))
        ),
        checkboxes = listOf(
            "Official" to (this?.official == true),
            "Modded" to (this?.modded == true)
        ),
        ranges = listOf(
            Triple("Player count", playerCount, (this?.playerCount)?.toInt()),
            Triple("Group limit", groupLimit, (this?.groupLimit)?.toInt()),
            Triple("Ranking", ranking, (this?.ranking)?.toInt())
        )
    )
}