package pl.cuyer.rusthub.data.network.model

import kotlinx.serialization.Serializable

@Serializable
enum class WipeScheduleDto {
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}