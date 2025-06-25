package pl.cuyer.rusthub.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.getImageByFileName

actual class NotificationPresenter(private val context: Context) {
    actual fun show(title: String, body: String) {
        buildOrderNotification(title, body)
    }

    private fun buildOrderNotification(title: String, body: String) {
        createNotificationChannel(NotificationManager.IMPORTANCE_DEFAULT)
        rusthubNotificationManager().notify(
            (title + body).hashCode(),
            notificationBuilder(title, body).build()
        )
    }

    private fun notificationBuilder(title: String, body: String): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelIdRusthub())
            .setSmallIcon(getImageByFileName("rusthub_logo").drawableResId)
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)
    }

    private fun createNotificationChannel(importance: Int) {
        with(rusthubNotificationManager()) {
            this.createNotificationChannel(
                NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    channelIdRusthub(),
                    importance
                ).apply {
                    description = channelDescription()
                }
            )
        }
    }

    private fun channelDescription(): String = SharedRes.strings.notification_channel_description.getString(context)

    private fun channelIdRusthub(): String = SharedRes.strings.notification_channel_name.getString(context)

    private fun rusthubNotificationManager(): NotificationManager =
        context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

    private fun createPendingIntent(): PendingIntent {
        val intent = Intent().apply {
            component = ComponentName(
                PACKAGE_NAME,
                MAIN_ACTIVITY
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }

    //TODO można dodać nawigację po kliknięciu do serwera

    companion object {
        const val MAIN_ACTIVITY = "pl.cuyer.rusthub.MainActivity"
        const val PACKAGE_NAME = "pl.cuyer.rusthub"
        const val DEFAULT_CHANNEL_ID = "default_channel"
    }
}
