package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FiltersOptions(
    val flags: List<Flag>,
    val maxRanking: Int,
    val maxPlayerCount: Int,
    val maxGroupLimit: Int,
    val maps: List<Maps>,
    val regions: List<Region>,
    val difficulty: List<Difficulty>,
    val wipeSchedules: List<WipeSchedule>
)
