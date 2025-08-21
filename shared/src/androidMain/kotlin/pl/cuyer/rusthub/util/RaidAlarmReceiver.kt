package pl.cuyer.rusthub.util

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.util.NotificationPresenter

class RaidAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val name = intent.getStringExtra(EXTRA_NAME) ?: return
        val notification = NotificationCompat.Builder(context, NotificationPresenter.DEFAULT_CHANNEL_ID)
            .setSmallIcon(getImageByFileName("rusthub_notification_icon").drawableResId)
            .setContentTitle(SharedRes.strings.raid_notification_title.getString(context))
            .setContentText(
                SharedRes.strings.raid_notification_body.getString(context, name)
            )
            .setAutoCancel(true)
            .build()
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(name.hashCode(), notification)
    }

    companion object {
        const val EXTRA_NAME = "extra_name"
    }
}
