package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.Raid

expect class AlarmScheduler {
    fun canScheduleExactAlarms(): Boolean
    fun requestExactAlarmPermission()
    fun schedule(raid: Raid)
    fun cancel(raid: Raid)
}

