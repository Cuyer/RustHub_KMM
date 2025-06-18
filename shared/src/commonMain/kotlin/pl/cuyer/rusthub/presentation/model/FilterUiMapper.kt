package pl.cuyer.rusthub.presentation.model

import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.displayName

fun ServerQuery.toUi(): FilterUi {
    val maps = Maps.entries.map { it.name }
    val flags = Flag.entries.map { it.displayName.uppercase() }
    val regions = Region.entries.map { it.name }
    val difficulties = Difficulty.entries.map { it.name }
    val schedules = WipeSchedule.entries.map { it.name }
    val order = Order.entries.map { it.name }

    return FilterUi(
        lists = listOf(
            Triple("Map", maps, maps.indexOf(this.map?.name ?: "")),
            Triple("Country", flags, flags.indexOf(this.flag?.displayName?.uppercase() ?: "")),
            Triple("Region", regions, regions.indexOf(this.region?.name ?: "")),
            Triple("Difficulty", difficulties, difficulties.indexOf(this.difficulty?.name ?: "")),
            Triple("Wipe Schedule", schedules, schedules.indexOf(this.wipeSchedule?.name ?: "")),
            Triple("Order", order, order.indexOf(this.order.name))
        ),
        checkboxes = listOf(
            "Official" to (this.official == true),
            "Modded" to (this.modded == true)
        ),
        ranges = listOf(
            Triple("Player count", 300, (this.playerCount ?: 0).toInt()),
            Triple("Group limit", 10, (this.groupLimit ?: 0).toInt()),
            Triple("Ranking", 3000, (this.ranking ?: 0.0).toInt())
        )
    )
}