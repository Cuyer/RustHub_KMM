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
import pl.cuyer.rusthub.domain.model.NotificationType

actual class NotificationPresenter(private val context: Context) {
    actual fun show(id: String, type: NotificationType) {
        buildNotification(type)
    }

    private fun buildNotification(type: NotificationType) {
        createNotificationChannel(type, NotificationManager.IMPORTANCE_DEFAULT)
        rusthubNotificationManager().notify(
            (type.name).hashCode(),
            notificationBuilder(type).build()
        )
    }

    private fun notificationBuilder(type: NotificationType): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId(type))
            .setSmallIcon(getImageByFileName("rusthub_logo").drawableResId)
            .setContentTitle(createTitle(type))
            .setContentText(createBody(type))
            .setContentIntent(createPendingIntent())
            .setAutoCancel(true)
    }

    private fun createNotificationChannel(type: NotificationType, importance: Int) {
        with(rusthubNotificationManager()) {
            val channelId = channelId(type)
            val channelName = channelName(type)
            val description = channelDescription(type)
            this.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName,
                    importance
                ).apply {
                    this.description = description
                }
            )
        }
    }

    private fun createBody(type: NotificationType): String = when (type) {
        NotificationType.MapWipe -> context.getString(
            SharedRes.strings.map_wipe_notification_body.resourceId,
            type.name
        )

        NotificationType.Wipe -> context.getString(
            SharedRes.strings.wipe_notification_body.resourceId,
            type.name
        )
    }

    private fun createTitle(type: NotificationType): String = when (type) {
        NotificationType.MapWipe -> SharedRes.strings.map_wipe_notification_title.getString(context)
        NotificationType.Wipe -> SharedRes.strings.wipe_notification_title.getString(context)
    }

    private fun channelDescription(type: NotificationType): String = when (type) {
        NotificationType.MapWipe -> SharedRes.strings.map_wipe_notification_channel_description.getString(context)
        NotificationType.Wipe -> SharedRes.strings.wipe_notification_channel_description.getString(context)
    }

    private fun channelName(type: NotificationType): String = when (type) {
        NotificationType.MapWipe -> SharedRes.strings.map_wipe_notification_channel_name.getString(context)
        NotificationType.Wipe -> SharedRes.strings.wipe_notification_channel_name.getString(context)
    }

    private fun channelId(type: NotificationType): String = when (type) {
        NotificationType.MapWipe -> NotificationType.MapWipe.name
        NotificationType.Wipe -> NotificationType.Wipe.name
    }

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
        const val PACKAGE_NAME = "pl.cuyer.rusthub.android"
    }
}
