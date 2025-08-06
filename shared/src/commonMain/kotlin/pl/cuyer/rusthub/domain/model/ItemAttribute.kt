package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import dev.icerock.moko.resources.StringResource
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.util.StringProvider

@Serializable
@Immutable
data class ItemAttribute(
    val type: ItemAttributeType,
    val value: String? = null,
)

@Serializable
@Immutable
enum class ItemAttributeType(val key: String) {
    HYDRATION("hydration"),
    CRAFTING_QUALITY("craftingQuality"),
    DURATION("duration"),
    HARVESTING_YIELD("harvestingYield"),
    TEMPERATURE("temperature"),
    MAX_CORE_TEMPERATURE("maxCoreTemperature"),
    CALORIES("calories"),
    HEALTH("health"),
    HUNTER_VISION("hunterVision"),
    VISION_CARE("visionCare"),
    MAX_HP("maxHp"),
    METABOLISM_BOOST("metabolismBoost"),
    CLOTTING("clotting"),
    COMFORT("comfort"),
    BETTER_GENE_CHANCE("betterGeneChance"),
    DIGESTION_BOOST("digestionBoost"),
    WOOD_YIELD("woodYield"),
    ORE_YIELD("oreYield"),
    SCRAP_YIELD("scrapYield"),
    RAD_RESISTANCE("radResistance");

    companion object {
        fun fromKey(key: String): ItemAttributeType? = values().find { it.key == key }
    }
}


fun ItemAttributeType.toNameRes(stringProvider: StringProvider): String {
    return when (this) {
        ItemAttributeType.HYDRATION -> stringProvider.get(SharedRes.strings.hydration)
        ItemAttributeType.CRAFTING_QUALITY -> stringProvider.get(SharedRes.strings.crafting_quality)
        ItemAttributeType.DURATION -> stringProvider.get(SharedRes.strings.duration)
        ItemAttributeType.HARVESTING_YIELD -> stringProvider.get(SharedRes.strings.harvesting_yield)
        ItemAttributeType.TEMPERATURE -> stringProvider.get(SharedRes.strings.temperature)
        ItemAttributeType.MAX_CORE_TEMPERATURE -> stringProvider.get(SharedRes.strings.max_core_temperature)
        ItemAttributeType.CALORIES -> stringProvider.get(SharedRes.strings.calories)
        ItemAttributeType.HEALTH -> stringProvider.get(SharedRes.strings.health)
        ItemAttributeType.HUNTER_VISION -> stringProvider.get(SharedRes.strings.hunter_vision)
        ItemAttributeType.VISION_CARE -> stringProvider.get(SharedRes.strings.vision_care)
        ItemAttributeType.MAX_HP -> stringProvider.get(SharedRes.strings.max_hp)
        ItemAttributeType.METABOLISM_BOOST -> stringProvider.get(SharedRes.strings.metabolism_boost)
        ItemAttributeType.CLOTTING -> stringProvider.get(SharedRes.strings.clotting)
        ItemAttributeType.COMFORT -> stringProvider.get(SharedRes.strings.comfort)
        ItemAttributeType.BETTER_GENE_CHANCE -> stringProvider.get(SharedRes.strings.better_gene_chance)
        ItemAttributeType.DIGESTION_BOOST -> stringProvider.get(SharedRes.strings.digestion_boost)
        ItemAttributeType.WOOD_YIELD -> stringProvider.get(SharedRes.strings.wood_yield)
        ItemAttributeType.ORE_YIELD -> stringProvider.get(SharedRes.strings.ore_yield)
        ItemAttributeType.SCRAP_YIELD -> stringProvider.get(SharedRes.strings.scrap_yield)
        ItemAttributeType.RAD_RESISTANCE -> stringProvider.get(SharedRes.strings.rad_resistance)
    }
}
