package pl.cuyer.rusthub.domain.model

import domain.models.server.battlemetrics.Wipe
import kotlinx.serialization.Serializable

@Serializable
enum class WipeSchedule {
    WEEKLY,
    BIWEEKLY,
    MONTHLY;

    companion object {
        /**
         * Picks a schedule based on the “weeks” flags:
         *  - 1 flagged week → WEEKLY
         *  - 5 flagged weeks → BIWEEKLY
         *  - 10 flagged weeks → MONTHLY
         *  - anything else → null
         */
        fun from(wipes: List<Wipe>): WipeSchedule? {
            // no data → can’t pick a schedule
            val spec = wipes.firstOrNull() ?: return null

            // sum of your [1,0,1,1,0]-style flags
            val flags = spec.weeks.sum()

            return when (flags) {
                1 -> MONTHLY
                5 -> WEEKLY
                10 -> BIWEEKLY
                else -> null
            }
        }
    }
}