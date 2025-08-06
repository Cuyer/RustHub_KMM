package pl.cuyer.rusthub.data.network.monument.mapper

import pl.cuyer.rusthub.data.network.monument.model.*
import pl.cuyer.rusthub.domain.model.*

fun MonumentDto.toDomain(): Monument {
    return Monument(
        name = name,
        slug = slug,
        iconUrl = iconUrl,
        attributes = attributes?.toDomain(),
        spawns = spawns?.toDomain(),
        usableEntities = usableEntities?.map { it.toDomain() },
        mining = mining?.toDomain(),
        puzzles = puzzles?.map { it.toDomain() },
        language = language?.toLanguage() ?: Language.ENGLISH
    )
}

fun MonumentAttributesDto.toDomain(): MonumentAttributes {
    return MonumentAttributes(
        type = type?.toMonumentType(),
        isSafezone = isSafezone,
        hasTunnelEntrance = hasTunnelEntrance,
        hasChinookDropZone = hasChinookDropZone,
        allowsPatrolHeliCrash = allowsPatrolHeliCrash,
        recyclers = recyclers,
        barrels = barrels,
        crates = crates,
        scientists = scientists,
        medianRadiationLevel = medianRadiationLevel,
        maxRadiationLevel = maxRadiationLevel,
        hasRadiation = hasRadiation
    )
}

private fun String.toMonumentType(): MonumentType? = when (this) {
    "Small" -> MonumentType.SMALL
    "Safe Zones" -> MonumentType.SAFE_ZONES
    "Oceanside" -> MonumentType.OCEANSIDE
    "Medium" -> MonumentType.MEDIUM
    "Roadside" -> MonumentType.ROADSIDE
    "Offshore" -> MonumentType.OFFSHORE
    "Large" -> MonumentType.LARGE
    else -> null
}

fun MonumentSpawnsDto.toDomain(): MonumentSpawns {
    return MonumentSpawns(
        container = container?.map { it.toDomain() },
        collectable = collectable?.map { it.toDomain() },
        scientist = scientist?.map { it.toDomain() },
        vehicle = vehicle?.map { it.toDomain() }
    )
}

fun SpawnGroupDto.toDomain(): SpawnGroup {
    return SpawnGroup(
        options = options?.map { it.toDomain() },
        amount = amount
    )
}

fun SpawnOptionDto.toDomain(): SpawnOption {
    return SpawnOption(name = name, chance = chance, image = image)
}

fun UsableEntityDto.toDomain(): UsableEntity {
    return UsableEntity(name = name, amount = amount, image = image)
}

fun MiningDto.toDomain(): Mining {
    return Mining(
        item = item?.toDomain(),
        productionItems = productionItems?.map { it.toDomain() },
        productionItemsAreChoices = productionItemsAreChoices,
        timePerFuelSeconds = timePerFuelSeconds
    )
}

fun MiningItemDto.toDomain(): MiningItem {
    return MiningItem(name = name, amount = amount, image = image)
}

fun MiningProductionDto.toDomain(): MiningProduction {
    return MiningProduction(name = name, amount = amount, image = image)
}

fun MonumentPuzzleDto.toDomain(): MonumentPuzzle {
    return MonumentPuzzle(
        needToBring = needToBring?.map { it.toDomain() },
        needToActivate = needToActivate?.map { it.toDomain() },
        entities = entities?.map { list -> list.map { it.toDomain() } }
    )
}

fun PuzzleRequirementDto.toDomain(): PuzzleRequirement {
    return PuzzleRequirement(name = name, amount = amount, image = image)
}

private fun String.toLanguage(): Language = when (lowercase()) {
    "pl" -> Language.POLISH
    "de" -> Language.GERMAN
    "fr" -> Language.FRENCH
    "ru" -> Language.RUSSIAN
    else -> Language.ENGLISH
}
