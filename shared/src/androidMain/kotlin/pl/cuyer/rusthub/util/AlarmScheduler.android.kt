package pl.cuyer.rusthub.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.domain.model.Raid
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual class AlarmScheduler(private val context: Context) {
    actual fun schedule(raid: Raid) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RaidAlarmReceiver::class.java).apply {
            putExtra(RaidAlarmReceiver.EXTRA_NAME, raid.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            raid.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = raid.dateTime.toInstant(TimeZone.currentSystemDefault()).toEpochMilliseconds()
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pendingIntent
        )
    }

    actual fun cancel(raid: Raid) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, RaidAlarmReceiver::class.java).apply {
            putExtra(RaidAlarmReceiver.EXTRA_NAME, raid.name)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            raid.id.hashCode(),
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_NO_CREATE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
