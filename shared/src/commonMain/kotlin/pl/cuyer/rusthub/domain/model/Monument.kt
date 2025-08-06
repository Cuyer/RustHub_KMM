package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@Immutable
data class Monument(
    val name: String? = null,
    val slug: String? = null,
    val attributes: MonumentAttributes? = null,
    val spawns: MonumentSpawns? = null,
    @SerialName("usableEntities") val usableEntities: List<UsableEntity>? = null,
    val mining: Mining? = null,
    val puzzles: List<MonumentPuzzle>? = null,
    val language: String? = null
)

@Serializable
@Immutable
data class MonumentAttributes(
    val type: MonumentType? = null,
    val isSafezone: Boolean? = null,
    val hasTunnelEntrance: Boolean? = null,
    val hasChinookDropZone: Boolean? = null,
    val allowsPatrolHeliCrash: Boolean? = null,
    val recyclers: Int? = null,
    val barrels: Int? = null,
    val crates: Int? = null,
    val scientists: Int? = null,
    val medianRadiationLevel: Double? = null,
    val maxRadiationLevel: Double? = null,
    val hasRadiation: Boolean? = null
)

@Serializable
@Immutable
data class MonumentSpawns(
    val container: List<SpawnGroup>? = null,
    val collectable: List<SpawnGroup>? = null,
    val scientist: List<SpawnGroup>? = null,
    val vehicle: List<SpawnGroup>? = null
)

@Serializable
@Immutable
data class SpawnGroup(
    val options: List<SpawnOption>? = null,
    val amount: Int? = null
)

@Serializable
@Immutable
data class SpawnOption(
    val name: String? = null,
    val chance: Double? = null,
    val image: String? = null
)

@Serializable
@Immutable
data class UsableEntity(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
@Immutable
data class Mining(
    val item: MiningItem? = null,
    val productionItems: List<MiningProduction>? = null,
    val productionItemsAreChoices: Boolean? = null,
    val timePerFuelSeconds: Int? = null
)

@Serializable
@Immutable
data class MiningItem(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
@Immutable
data class MiningProduction(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
@Immutable
data class MonumentPuzzle(
    val needToBring: List<PuzzleRequirement>? = null,
    val needToActivate: List<PuzzleRequirement>? = null,
    val entities: List<List<SpawnGroup>>? = null
)

@Serializable
@Immutable
data class PuzzleRequirement(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
@Immutable
enum class MonumentType {
    @SerialName("Small") SMALL,
    @SerialName("Safe Zones") SAFE_ZONES,
    @SerialName("Oceanside") OCEANSIDE,
    @SerialName("Medium") MEDIUM,
    @SerialName("Roadside") ROADSIDE,
    @SerialName("Offshore") OFFSHORE,
    @SerialName("Large") LARGE
}

@Serializable
@Immutable
enum class MonumentSyncState { PENDING, DONE }
