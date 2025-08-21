package pl.cuyer.rusthub.data.network.raid.mapper

import kotlinx.datetime.LocalDateTime
import pl.cuyer.rusthub.data.network.raid.model.RaidDto
import pl.cuyer.rusthub.domain.model.Raid

fun RaidDto.toDomain(): Raid {
    return Raid(
        id = id,
        name = name,
        dateTime = LocalDateTime.parse(dateTime),
        steamIds = steamIds,
        description = description
    )
}

fun Raid.toDto(): RaidDto {
    return RaidDto(
        id = id,
        name = name,
        dateTime = dateTime.toString(),
        steamIds = steamIds,
        description = description
    )
}

