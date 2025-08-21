package pl.cuyer.rusthub.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import pl.cuyer.rusthub.domain.model.Raid
import kotlin.time.ExperimentalTime
import androidx.core.net.toUri

@OptIn(ExperimentalTime::class)
actual class AlarmScheduler(private val context: Context) {

    private fun buildIntent(raid: Raid): Intent =
        Intent(context, RaidAlarmReceiver::class.java).apply {
            // Stable identity: requestCode can be 0 when using unique data URI
            data = "rusthub://raid-alarm/${raid.id}".toUri()
            putExtra(RaidAlarmReceiver.EXTRA_NAME, raid.name)
        }

    actual fun canScheduleExactAlarms(): Boolean {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        return Build.VERSION.SDK_INT < 31 || alarmManager.canScheduleExactAlarms()
    }

    actual fun requestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= 31) {
            context.startActivity(
                Intent(ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            )
        }
    }

    actual fun schedule(raid: Raid) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val triggerAt = raid.dateTime
            .toInstant(TimeZone.currentSystemDefault())
            .toEpochMilliseconds()

        if (triggerAt <= System.currentTimeMillis()) {
            // Too late; avoid scheduling past alarms
            return
        }

        val pi = PendingIntent.getBroadcast(
            context,
            0, // identity comes from Intent.data
            buildIntent(raid),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            triggerAt,
            pi
        )
    }

    actual fun cancel(raid: Raid) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val pi = PendingIntent.getBroadcast(
            context,
            0,
            buildIntent(raid), // MUST match schedule() intent (same data URI)
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pi != null) alarmManager.cancel(pi)
    }
}