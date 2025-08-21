package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.Raid

expect class AlarmScheduler {
    fun schedule(raid: Raid)
}

