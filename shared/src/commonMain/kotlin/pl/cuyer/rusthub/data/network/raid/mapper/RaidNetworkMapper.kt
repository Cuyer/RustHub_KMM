package pl.cuyer.rusthub.data.network.raid.mapper

import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import pl.cuyer.rusthub.data.network.raid.model.RaidDto
import pl.cuyer.rusthub.domain.model.Raid

fun RaidDto.toDomain(): Raid {
    return Raid(
        id = id,
        name = name,
        dateTime = dateTime.toLocalDateTime(TimeZone.currentSystemDefault()),
        steamIds = steamIds,
        description = description
    )
}

fun Raid.toDto(): RaidDto {
    return RaidDto(
        id = id,
        name = name,
        dateTime = dateTime.toInstant(TimeZone.currentSystemDefault()),
        steamIds = steamIds,
        description = description
    )
}

