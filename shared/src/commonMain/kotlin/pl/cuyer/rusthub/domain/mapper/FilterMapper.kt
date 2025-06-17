package pl.cuyer.rusthub.domain.mapper

import database.FiltersEntity
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.ServerQuery

fun FiltersEntity.toServerQuery(): ServerQuery {
    return ServerQuery(
        wipe = wipe?.let { Instant.parse(it) },
        ranking = ranking,
        modded = modded == 1L,
        playerCount = player_count,
        flag = server_flag,
        region = region,
        groupLimit = group_limit,
        difficulty = difficulty,
        wipeSchedule = wipe_schedule,
        official = is_official == 1L,
        order = sort_order ?: Order.WIPE,
        map = map_name
    )
}