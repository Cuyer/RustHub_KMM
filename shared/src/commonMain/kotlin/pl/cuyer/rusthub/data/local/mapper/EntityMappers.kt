package pl.cuyer.rusthub.data.local.mapper

import database.FiltersDifficultyEntity
import database.FiltersEntity
import database.FiltersFlagEntity
import database.FiltersMapEntity
import database.FiltersOptionsEntity
import database.FiltersRegionEntity
import database.FiltersWipeScheduleEntity
import database.RemoteKeyEntity
import database.ServerEntity
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.local.model.DifficultyEntity
import pl.cuyer.rusthub.data.local.model.FlagEntity
import pl.cuyer.rusthub.data.local.model.MapsEntity
import pl.cuyer.rusthub.data.local.model.OrderEntity
import pl.cuyer.rusthub.data.local.model.RegionEntity
import pl.cuyer.rusthub.data.local.model.ServerStatusEntity
import pl.cuyer.rusthub.data.local.model.WipeScheduleEntity
import pl.cuyer.rusthub.data.local.model.WipeTypeEntity
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.RemoteKey
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.WipeType

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

fun ServerStatusEntity?.toDomain(): ServerStatus? = this?.let { ServerStatus.valueOf(it.name) }
fun ServerStatus?.toEntity(): ServerStatusEntity? =
    this?.let { ServerStatusEntity.valueOf(it.name) }

fun WipeTypeEntity?.toDomain(): WipeType? = this?.let { WipeType.valueOf(it.name) }
fun WipeType?.toEntity(): WipeTypeEntity? = this?.let { WipeTypeEntity.valueOf(it.name) }

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
        description = description,
        serverStatus = server_status.toDomain(),
        wipeType = wipe_type.toDomain()
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

fun FiltersOptionsEntity?.toDomain(
    flags: List<Flag?>,
    maps: List<Maps?>,
    regions: List<Region?>,
    difficulty: List<Difficulty?>,
    wipeSchedules: List<WipeSchedule?>
): FiltersOptions {
    return FiltersOptions(
        maxRanking = this?.max_ranking?.toInt() ?: 0,
        maxPlayerCount = this?.max_player_count?.toInt() ?: 0,
        maxGroupLimit = this?.max_group_limit?.toInt() ?: 0,
        flags = flags.filterNotNull(),
        maps = maps.filterNotNull(),
        regions = regions.filterNotNull(),
        difficulty = difficulty.filterNotNull(),
        wipeSchedules = wipeSchedules.filterNotNull()
    )
}

fun FiltersDifficultyEntity?.toDomain(): Difficulty? = this?.let { Difficulty.valueOf(it.label) }

fun FiltersFlagEntity?.toDomain(): Flag? = this?.let { Flag.valueOf(it.label) }

fun FiltersMapEntity?.toDomain(): Maps? = this?.let { Maps.valueOf(it.label) }

fun FiltersRegionEntity?.toDomain(): Region? = this?.let { Region.valueOf(it.label) }

fun FiltersWipeScheduleEntity?.toDomain(): WipeSchedule? =
    this?.let { WipeSchedule.valueOf(it.label) }

import database.SearchQueryEntity
import pl.cuyer.rusthub.domain.model.SearchQuery

fun SearchQueryEntity.toDomain(): SearchQuery = SearchQuery(query)
fun SearchQuery.toEntity(): SearchQueryEntity = SearchQueryEntity(query)

