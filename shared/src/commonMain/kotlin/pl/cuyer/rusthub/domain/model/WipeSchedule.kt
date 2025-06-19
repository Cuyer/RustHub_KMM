package pl.cuyer.rusthub.domain.model

enum class WipeSchedule {
    WEEKLY,
    BIWEEKLY,
    MONTHLY;

    companion object {
        fun fromDisplayName(name: String): WipeSchedule? =
            WipeSchedule.entries.firstOrNull { it.displayName == name }
    }
}

val WipeSchedule.displayName: String
    get() = this.name.lowercase().replaceFirstChar { it.uppercase() }