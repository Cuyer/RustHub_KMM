package pl.cuyer.rusthub.data.network.item.mapper

import pl.cuyer.rusthub.data.network.item.model.ItemCategoryDto
import pl.cuyer.rusthub.data.network.item.model.ItemLanguageDto
import pl.cuyer.rusthub.data.network.item.model.RustItemDto
import pl.cuyer.rusthub.data.network.item.model.LootingDto
import pl.cuyer.rusthub.data.network.item.model.LootAmountDto
import pl.cuyer.rusthub.data.network.item.model.CraftingDto
import pl.cuyer.rusthub.data.network.item.model.CraftingIngredientDto
import pl.cuyer.rusthub.data.network.item.model.CraftingRecipeDto
import pl.cuyer.rusthub.data.network.item.model.ResearchTableCostDto
import pl.cuyer.rusthub.data.network.item.model.TechTreeCostDto
import pl.cuyer.rusthub.data.network.item.model.RecyclingDto
import pl.cuyer.rusthub.data.network.item.model.RecyclerDto
import pl.cuyer.rusthub.data.network.item.model.RecyclerOutputDto
import pl.cuyer.rusthub.data.network.item.model.RaidingDto
import pl.cuyer.rusthub.data.network.item.model.StartingItemDto
import pl.cuyer.rusthub.data.network.item.model.RaidItemDto
import pl.cuyer.rusthub.data.network.item.model.RaidResourceDto
import pl.cuyer.rusthub.data.network.item.model.LootContentDto
import pl.cuyer.rusthub.data.network.item.model.WhereToFindDto
import pl.cuyer.rusthub.data.network.item.model.TableRecipeDto
import pl.cuyer.rusthub.data.network.item.model.TableRecipeIngredientDto
import pl.cuyer.rusthub.data.network.item.model.ItemsResponseDto
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.ItemsResponse
import pl.cuyer.rusthub.domain.model.Looting
import pl.cuyer.rusthub.domain.model.LootAmount
import pl.cuyer.rusthub.domain.model.Crafting
import pl.cuyer.rusthub.domain.model.CraftingIngredient
import pl.cuyer.rusthub.domain.model.CraftingRecipe
import pl.cuyer.rusthub.domain.model.ResearchTableCost
import pl.cuyer.rusthub.domain.model.TechTreeCost
import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.model.Recycler
import pl.cuyer.rusthub.domain.model.RecyclerOutput
import pl.cuyer.rusthub.domain.model.Raiding
import pl.cuyer.rusthub.domain.model.StartingItem
import pl.cuyer.rusthub.domain.model.RaidItem
import pl.cuyer.rusthub.domain.model.RaidResource
import pl.cuyer.rusthub.domain.model.LootContent
import pl.cuyer.rusthub.domain.model.WhereToFind
import pl.cuyer.rusthub.domain.model.TableRecipe
import pl.cuyer.rusthub.domain.model.TableRecipeIngredient
import pl.cuyer.rusthub.domain.model.ItemAttribute
import pl.cuyer.rusthub.domain.model.ItemAttributeType

fun RustItemDto.toDomain(): RustItem {
    return RustItem(
        slug = slug,
        url = url,
        name = name,
        description = description,
        image = image,
        stackSize = stackSize,
        health = health,
        attributes = attributes?.mapNotNull { (key, value) ->
            ItemAttributeType.fromKey(key)?.let { ItemAttribute(it, value) }
        },
        categories = categories?.map { it.toDomain() },
        looting = looting?.map { it.toDomain() },
        lootContents = lootContents?.map { it.toDomain() },
        whereToFind = whereToFind?.map { it.toDomain() },
        crafting = crafting?.toDomain(),
        tableRecipe = tableRecipe?.toDomain(),
        recycling = recycling?.toDomain(),
        raiding = raiding?.map { it.toDomain() },
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
        ItemCategoryDto.WORLD -> ItemCategory.WORLD
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
        ItemLanguageDto.PT -> Language.PORTUGUESE
        ItemLanguageDto.ES -> Language.SPANISH
        ItemLanguageDto.UK -> Language.UKRAINIAN
    }
}

fun LootingDto.toDomain(): Looting {
    return Looting(
        from = from,
        image = image,
        chance = chance,
        amount = amount?.let { LootAmount(it.min, it.max) }
    )
}

fun CraftingDto.toDomain(): Crafting {
    return Crafting(
        craftingRecipe = craftingRecipe?.toDomain(),
        researchTableCost = researchTableCost?.toDomain(),
        techTreeCost = techTreeCost?.toDomain()
    )
}

fun CraftingRecipeDto.toDomain(): CraftingRecipe {
    return CraftingRecipe(
        ingredients = ingredients?.map { it.toDomain() },
        outputAmount = outputAmount,
        outputImage = outputImage,
        outputName = outputName
    )
}

fun CraftingIngredientDto.toDomain(): CraftingIngredient {
    return CraftingIngredient(
        image = image,
        name = name,
        amount = amount
    )
}

fun ResearchTableCostDto.toDomain(): ResearchTableCost {
    return ResearchTableCost(
        tableImage = tableImage,
        tableName = tableName,
        itemImage = itemImage,
        itemName = itemName,
        itemAmount = itemAmount,
        scrapImage = scrapImage,
        scrapName = scrapName,
        scrapAmount = scrapAmount,
        outputImage = outputImage,
        outputName = outputName
    )
}

fun TechTreeCostDto.toDomain(): TechTreeCost {
    return TechTreeCost(
        workbenchImage = workbenchImage,
        workbenchName = workbenchName,
        scrapImage = scrapImage,
        scrapName = scrapName,
        scrapAmount = scrapAmount,
        outputName = outputName
    )
}

fun TableRecipeDto.toDomain(): TableRecipe {
    return TableRecipe(
        tableImage = tableImage,
        tableName = tableName,
        ingredients = ingredients?.map { it.toDomain() },
        outputImage = outputImage,
        outputName = outputName,
        outputAmount = outputAmount,
        totalCost = totalCost?.map { it.toDomain() }
    )
}

fun TableRecipeIngredientDto.toDomain(): TableRecipeIngredient {
    return TableRecipeIngredient(
        image = image,
        name = name,
        amount = amount,
    )
}

fun RecyclingDto.toDomain(): Recycling {
    return Recycling(
        radtownRecycler = radtownRecycler?.toDomain(),
        safezoneRecycler = safezoneRecycler?.toDomain()
    )
}

fun RecyclerDto.toDomain(): Recycler {
    return Recycler(
        image = image,
        guarantedOutput = guarantedOutput?.map { it.toDomain() },
        extraChanceOutput = extraChanceOutput?.map { it.toDomain() }
    )
}

fun RecyclerOutputDto.toDomain(): RecyclerOutput {
    return RecyclerOutput(
        image = image,
        name = name,
        amount = amount
    )
}

fun RaidingDto.toDomain(): Raiding {
    return Raiding(
        startingItem = startingItem?.toDomain(),
        timeToRaid = timeToRaid,
        amount = amount?.map { it.toDomain() },
        rawMaterialCost = rawMaterialCost?.map { it.toDomain() }
    )
}

fun StartingItemDto.toDomain(): StartingItem = StartingItem(icon = icon, name = name)

fun RaidItemDto.toDomain(): RaidItem = RaidItem(icon = icon, name = name, amount = amount)

fun RaidResourceDto.toDomain(): RaidResource = RaidResource(
    icon = icon,
    name = name,
    amount = amount,
    mixingTableAmount = mixingTableAmount
)

fun LootContentDto.toDomain(): LootContent = LootContent(
    spawn = spawn,
    image = image,
    stack = stack?.let { LootAmount(it.min, it.max) },
    chance = chance,
    amount = amount?.let { LootAmount(it.min, it.max) }
)

fun WhereToFindDto.toDomain(): WhereToFind = WhereToFind(
    place = place,
    image = image,
    amount = amount?.let { LootAmount(it.min, it.max) }
)

fun ItemsResponseDto.toDomain(): ItemsResponse = ItemsResponse(
    page = page,
    size = size,
    totalPages = totalPages,
    totalItems = totalItems,
    items = items.map { it.toDomain() }
)
