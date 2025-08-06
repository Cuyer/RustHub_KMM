package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

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
    DIGESTION_BOOST("digestionBoost");

    companion object {
        fun fromKey(key: String): ItemAttributeType? = values().find { it.key == key }
    }
}

