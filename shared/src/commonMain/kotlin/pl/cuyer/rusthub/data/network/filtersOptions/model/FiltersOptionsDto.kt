package pl.cuyer.rusthub.data.network.filtersOptions.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FiltersOptionsDto(
    @SerialName("flags")
    val flags: List<FlagDto>,
    @SerialName("max_ranking")
    val maxRanking: Int,
    @SerialName("max_player_count")
    val maxPlayerCount: Int,
    @SerialName("max_group_limit")
    val maxGroupLimit: Int,
    @SerialName("maps")
    val maps: List<MapsDto>,
    @SerialName("regions")
    val regions: List<RegionDto>,
    @SerialName("difficulty")
    val difficulty: List<DifficultyDto>,
    @SerialName("wipe_schedules")
    val wipeSchedules: List<WipeScheduleDto>
)
