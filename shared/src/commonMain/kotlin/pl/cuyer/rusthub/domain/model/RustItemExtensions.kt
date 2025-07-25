package pl.cuyer.rusthub.domain.model

fun Crafting.hasContent(): Boolean {
    return craftingRecipe?.hasContent() == true ||
        researchTableCost?.hasContent() == true ||
        techTreeCost?.hasContent() == true
}

fun CraftingRecipe.hasContent(): Boolean {
    return !ingredients.isNullOrEmpty() ||
        outputAmount != null ||
        outputImage != null ||
        outputName != null
}

fun ResearchTableCost.hasContent(): Boolean {
    return tableImage != null ||
        tableName != null ||
        itemImage != null ||
        itemName != null ||
        itemAmount != null ||
        scrapImage != null ||
        scrapName != null ||
        scrapAmount != null ||
        outputImage != null ||
        outputName != null
}

fun TechTreeCost.hasContent(): Boolean {
    return workbenchImage != null ||
        workbenchName != null ||
        scrapImage != null ||
        scrapName != null ||
        scrapAmount != null ||
        outputName != null
}

fun Recycler.hasContent(): Boolean {
    return image != null ||
        !guarantedOutput.isNullOrEmpty() ||
        !extraChanceOutput.isNullOrEmpty()
}

fun Recycling.hasContent(): Boolean {
    return radtownRecycler?.hasContent() == true ||
        safezoneRecycler?.hasContent() == true
}

fun StartingItem.hasContent(): Boolean {
    return icon != null || name != null
}

fun Raiding.hasContent(): Boolean {
    return startingItem?.hasContent() == true ||
        timeToRaid != null ||
        !amount.isNullOrEmpty() ||
        !rawMaterialCost.isNullOrEmpty()
}

