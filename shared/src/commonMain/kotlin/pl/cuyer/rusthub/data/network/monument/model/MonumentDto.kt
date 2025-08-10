package pl.cuyer.rusthub.data.network.monument.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MonumentDto(
    val name: String? = null,
    val slug: String? = null,
    val iconUrl: String? = null,
    @SerialName("mapUrls") val mapUrls: List<String>? = null,
    val attributes: MonumentAttributesDto? = null,
    val spawns: MonumentSpawnsDto? = null,
    @SerialName("usableEntities") val usableEntities: List<UsableEntityDto>? = null,
    val mining: MiningDto? = null,
    val puzzles: List<MonumentPuzzleDto>? = null,
    val language: String? = null
)

@Serializable
data class MonumentAttributesDto(
    val type: String? = null,
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
data class MonumentSpawnsDto(
    val container: List<SpawnGroupDto>? = null,
    val collectable: List<SpawnGroupDto>? = null,
    val scientist: List<SpawnGroupDto>? = null,
    val vehicle: List<SpawnGroupDto>? = null
)

@Serializable
data class SpawnGroupDto(
    val options: List<SpawnOptionDto>? = null,
    val amount: Int? = null
)

@Serializable
data class SpawnOptionDto(
    val name: String? = null,
    val chance: Double? = null,
    val image: String? = null
)

@Serializable
data class UsableEntityDto(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
data class MiningDto(
    val item: MiningItemDto? = null,
    val productionItems: List<MiningProductionDto>? = null,
    val productionItemsAreChoices: Boolean? = null,
    val timePerFuelSeconds: Int? = null
)

@Serializable
data class MiningItemDto(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
data class MiningProductionDto(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)

@Serializable
data class MonumentPuzzleDto(
    val needToBring: List<PuzzleRequirementDto>? = null,
    val needToActivate: List<PuzzleRequirementDto>? = null,
    val entities: List<List<SpawnGroupDto>>? = null
)

@Serializable
data class PuzzleRequirementDto(
    val name: String? = null,
    val amount: Int? = null,
    val image: String? = null
)
