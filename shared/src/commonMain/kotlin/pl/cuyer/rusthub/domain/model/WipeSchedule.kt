package pl.cuyer.rusthub.domain.model

enum class WipeSchedule {
    WEEKLY,
    BIWEEKLY,
    MONTHLY
}

val WipeSchedule.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }