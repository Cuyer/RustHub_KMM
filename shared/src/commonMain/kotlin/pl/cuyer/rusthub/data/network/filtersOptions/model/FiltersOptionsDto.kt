package pl.cuyer.rusthub.data.network.filtersOptions.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import pl.cuyer.rusthub.data.network.model.DifficultyDto
import pl.cuyer.rusthub.data.network.model.FlagDto
import pl.cuyer.rusthub.data.network.model.MapsDto
import pl.cuyer.rusthub.data.network.model.RegionDto
import pl.cuyer.rusthub.data.network.model.WipeScheduleDto

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
