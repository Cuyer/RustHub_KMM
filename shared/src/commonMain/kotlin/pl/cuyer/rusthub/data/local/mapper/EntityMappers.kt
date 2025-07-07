package pl.cuyer.rusthub.data.local.mapper

import database.FiltersDifficultyEntity
import database.FiltersEntity
import database.FiltersFlagEntity
import database.FiltersMapEntity
import database.FiltersOptionsEntity
import database.FiltersRegionEntity
import database.FiltersWipeScheduleEntity
import database.RemoteKeyEntity
import database.SearchQueryEntity
import database.ServerEntity
import database.UserEntity
import database.SettingsEntity
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.local.model.DifficultyEntity
import pl.cuyer.rusthub.data.local.model.FlagEntity
import pl.cuyer.rusthub.data.local.model.MapsEntity
import pl.cuyer.rusthub.data.local.model.OrderEntity
import pl.cuyer.rusthub.data.local.model.RegionEntity
import pl.cuyer.rusthub.data.local.model.ServerStatusEntity
import pl.cuyer.rusthub.data.local.model.ServerFilterEntity
import pl.cuyer.rusthub.data.local.model.WipeScheduleEntity
import pl.cuyer.rusthub.data.local.model.WipeTypeEntity
import pl.cuyer.rusthub.data.local.model.LanguageEntity
import pl.cuyer.rusthub.data.local.model.ThemeEntity
import pl.cuyer.rusthub.domain.model.Difficulty
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.model.Flag
import pl.cuyer.rusthub.domain.model.Maps
import pl.cuyer.rusthub.domain.model.Order
import pl.cuyer.rusthub.domain.model.Region
import pl.cuyer.rusthub.domain.model.RemoteKey
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.model.ServerFilter
import pl.cuyer.rusthub.domain.model.ServerStatus
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.WipeSchedule
import pl.cuyer.rusthub.domain.model.WipeType
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Settings
import pl.cuyer.rusthub.domain.model.Theme

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

fun ServerFilterEntity?.toDomain(): ServerFilter? = this?.let { ServerFilter.valueOf(it.name) }
fun ServerFilter?.toEntity(): ServerFilterEntity? = this?.let { ServerFilterEntity.valueOf(it.name) }

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
        map = map_name.toDomain(),
        filter = filter.toDomain() ?: ServerFilter.ALL
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
        wipeType = wipe_type.toDomain(),
        blueprints = blueprints == 1L,
        kits = kits == 1L,
        decay = decay?.toFloat(),
        upkeep = upkeep?.toFloat(),
        rates = rates?.toInt(),
        seed = seed?.toLong(),
        mapSize = map_size?.toInt(),
        monuments = monuments?.toInt(),
        averageFps = average_fps?.toLong(),
        pve = pve == 1L,
        website = website,
        isPremium = is_premium == 1L,
        mapUrl = map_url,
        headerImage = header_image,
        isFavorite = favourite == 1L,
        isSubscribed = subscribed == 1L,
        nextWipe = rust_next_wipe?.let { Instant.parse(it) },
        nextMapWipe = rust_next_map_wipe?.let { Instant.parse(it) }
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

fun SearchQueryEntity.toDomain(): SearchQuery = SearchQuery(id, query, Instant.parse(timestamp))

fun UserEntity.toUser(): User = User(
    email,
    username,
    access_token,
    refresh_token,
    AuthProvider.valueOf(provider),
    subscribed = subscribed == 1L
)




fun ThemeEntity?.toDomain(): Theme? = this?.let { Theme.valueOf(it.name) }
fun Theme?.toEntity(): ThemeEntity? = this?.let { ThemeEntity.valueOf(it.name) }

fun LanguageEntity?.toDomain(): Language? = this?.let { Language.valueOf(it.name) }
fun Language?.toEntity(): LanguageEntity? = this?.let { LanguageEntity.valueOf(it.name) }

fun SettingsEntity.toSettings(): Settings = Settings(
    theme = theme.toDomain() ?: Theme.SYSTEM,
    language = language.toDomain() ?: Language.ENGLISH
)
