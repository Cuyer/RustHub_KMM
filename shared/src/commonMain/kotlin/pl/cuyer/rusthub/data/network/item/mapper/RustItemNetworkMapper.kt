package pl.cuyer.rusthub.data.network.item.mapper

import pl.cuyer.rusthub.data.network.item.model.ItemCategoryDto
import pl.cuyer.rusthub.data.network.item.model.ItemLanguageDto
import pl.cuyer.rusthub.data.network.item.model.RustItemDto
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.RustItem

fun RustItemDto.toDomain(): RustItem {
    return RustItem(
        slug = slug,
        url = url,
        name = name,
        description = description,
        image = image,
        stackSize = stackSize,
        health = health,
        categories = categories?.map { it.toDomain() },
        shortName = shortName,
        id = id,
        iconUrl = iconUrl,
        language = language?.toDomain(),
    )
}

fun ItemCategoryDto.toDomain(): ItemCategory {
    return when (this) {
        ItemCategoryDto.WEAPONS -> ItemCategory.WEAPONS
        ItemCategoryDto.AMMO -> ItemCategory.AMMO
        ItemCategoryDto.CLOTHES -> ItemCategory.CLOTHES
        ItemCategoryDto.FUN -> ItemCategory.FUN
        ItemCategoryDto.ITEMS -> ItemCategory.ITEMS
        ItemCategoryDto.FOOD -> ItemCategory.FOOD
        ItemCategoryDto.COMPONENTS -> ItemCategory.COMPONENTS
        ItemCategoryDto.MEDICAL -> ItemCategory.MEDICAL
        ItemCategoryDto.ELECTRICAL -> ItemCategory.ELECTRICAL
        ItemCategoryDto.TOOLS -> ItemCategory.TOOLS
        ItemCategoryDto.CONSTRUCTION -> ItemCategory.CONSTRUCTION
        ItemCategoryDto.RESOURCES -> ItemCategory.RESOURCES
        ItemCategoryDto.MISC -> ItemCategory.MISC
        ItemCategoryDto.TRAPS -> ItemCategory.TRAPS
        ItemCategoryDto.UNCATEGORIZED -> ItemCategory.UNCATEGORIZED
    }
}

fun ItemLanguageDto.toDomain(): Language {
    return when (this) {
        ItemLanguageDto.FR -> Language.FRENCH
        ItemLanguageDto.EN -> Language.ENGLISH
        ItemLanguageDto.DE -> Language.GERMAN
        ItemLanguageDto.RU -> Language.RUSSIAN
    }
}
