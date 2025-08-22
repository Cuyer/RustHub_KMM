package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.model.Raid

actual class AlarmScheduler {
    fun canScheduleExactAlarms(): Boolean = true
    fun requestExactAlarmPermission() {}
    actual fun schedule(raid: Raid) {}
    actual fun cancel(raid: Raid) {}
}
