package pl.cuyer.rusthub.data.network.filtersOptions.model

import kotlinx.serialization.Serializable

@Serializable
enum class WipeScheduleDto {
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}