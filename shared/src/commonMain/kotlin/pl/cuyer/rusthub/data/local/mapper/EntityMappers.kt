package pl.cuyer.rusthub.data.local.mapper

import database.FiltersEntity
import database.ServerEntity
import database.RemoteKeyEntity
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.local.model.DifficultyEntity
import pl.cuyer.rusthub.data.local.model.FlagEntity
import pl.cuyer.rusthub.data.local.model.MapsEntity
import pl.cuyer.rusthub.data.local.model.OrderEntity
import pl.cuyer.rusthub.data.local.model.RegionEntity
import pl.cuyer.rusthub.data.local.model.WipeScheduleEntity
import pl.cuyer.rusthub.domain.model.*

fun DifficultyEntity?.toDomain(): Difficulty? = this?.let { Difficulty.valueOf(it.name) }
fun Difficulty?.toEntity(): DifficultyEntity? = this?.let { DifficultyEntity.valueOf(it.name) }

fun FlagEntity?.toDomain(): Flag? = this?.let { Flag.valueOf(it.name) }
fun Flag?.toEntity(): FlagEntity? = this?.let { FlagEntity.valueOf(it.name) }

fun MapsEntity?.toDomain(): Maps? = this?.let { Maps.valueOf(it.name) }
fun Maps?.toEntity(): MapsEntity? = this?.let { MapsEntity.valueOf(it.name) }

fun RegionEntity?.toDomain(): Region? = this?.let { Region.valueOf(it.name) }
fun Region?.toEntity(): RegionEntity? = this?.let { RegionEntity.valueOf(it.name) }

fun WipeScheduleEntity?.toDomain(): WipeSchedule? = this?.let { WipeSchedule.valueOf(it.name) }
fun WipeSchedule?.toEntity(): WipeScheduleEntity? = this?.let { WipeScheduleEntity.valueOf(it.name) }

fun OrderEntity?.toDomain(): Order? = this?.let { Order.valueOf(it.name) }
fun Order?.toEntity(): OrderEntity? = this?.let { OrderEntity.valueOf(it.name) }

fun FiltersEntity.toServerQuery(): ServerQuery {
    return ServerQuery(
        wipe = wipe?.let { Instant.parse(it) },
        ranking = ranking,
        modded = modded == 1L,
        playerCount = player_count,
        flag = server_flag.toDomain(),
        region = region.toDomain(),
        groupLimit = group_limit,
        difficulty = difficulty.toDomain(),
        wipeSchedule = wipe_schedule.toDomain(),
        official = is_official == 1L,
        order = sort_order.toDomain() ?: Order.WIPE,
        map = map_name.toDomain()
    )
}

fun ServerEntity.toServerInfo(): ServerInfo {
    return ServerInfo(
        id = id,
        name = name,
        wipe = wipe?.let { Instant.parse(it) },
        ranking = ranking,
        modded = modded == 1L ,
        playerCount = player_count,
        serverCapacity = capacity,
        mapName = map_name.toDomain(),
        cycle = cycle,
        serverFlag = server_flag.toDomain(),
        region = region.toDomain(),
        maxGroup = max_group,
        difficulty = difficulty.toDomain(),
        wipeSchedule = wipe_schedule.toDomain(),
        isOfficial = is_official == 1L,
        serverIp = ip,
        mapImage = map_image,
        description = description
    )
}

fun RemoteKeyEntity.toDomain(): RemoteKey {
    return RemoteKey(
        id = id,
        nextPage = next_page,
        lastUpdated = last_updated
    )
}

fun RemoteKey.toEntity(): RemoteKeyEntity {
    return RemoteKeyEntity(
        id = id,
        next_page = nextPage,
        last_updated = lastUpdated
    )
}
