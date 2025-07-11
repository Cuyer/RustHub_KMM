package pl.cuyer.rusthub.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.common.getImageByFileName
import pl.cuyer.rusthub.domain.model.NotificationType
import kotlin.random.Random
import kotlinx.datetime.Instant

actual class NotificationPresenter(private val context: Context) {
    actual fun show(name: String, type: NotificationType, timestamp: String) {
        buildNotification(type, name, timestamp)
    }

    fun createDefaultChannels() {
        createDefaultChannel()
        NotificationType.entries.forEach { type ->
            createNotificationChannel(type, NotificationManager.IMPORTANCE_DEFAULT)
        }
    }

    private fun createDefaultChannel() {
        with(rusthubNotificationManager()) {
            this.createNotificationChannel(
                NotificationChannel(
                    DEFAULT_CHANNEL_ID,
                    SharedRes.strings.notification_channel_name.getString(context),
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        SharedRes.strings.notification_channel_description.getString(context)
                }
            )
        }
    }

    private fun buildNotification(type: NotificationType, name: String, timestamp: String) {
        rusthubNotificationManager().notify(
            (type.name + name + timestamp).hashCode(),
            notificationBuilder(type, name, timestamp).build()
        )
    }

    private fun notificationBuilder(
        type: NotificationType,
        name: String,
        timestamp: String
    ): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, channelId(type))
            .setSmallIcon(getImageByFileName("rusthub_notification_icon").drawableResId)
            .setContentTitle(createTitle(type))
            .setContentText(createBody(name, type, timestamp))
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

    private fun createBody(name: String, type: NotificationType, timestamp: String): String {
        val localTime = parseAndFormatWipeTime(timestamp)

        return when (type) {
            NotificationType.MapWipe -> context.getString(
                SharedRes.strings.map_wipe_notification_body.resourceId,
                name,
                localTime
            )

            NotificationType.Wipe -> context.getString(
                SharedRes.strings.wipe_notification_body.resourceId,
                name,
                localTime
            )
        }
    }

    fun parseAndFormatWipeTime(wipeTimeString: String): String {
        val instant = Instant.parse(wipeTimeString)
        val localDateTime = instant.toLocalDateTime(TimeZone.currentSystemDefault())
        return "%02d:%02d".format(localDateTime.hour, localDateTime.minute)
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
        val packageName = context.packageName
        val intent = Intent().apply {
            component = ComponentName(
                packageName,
                "pl.cuyer.rusthub.android.MainActivity"
            )
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        return PendingIntent.getActivity(
            context,
            Random.nextInt(),
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        const val DEFAULT_CHANNEL_ID = "notification_channel_name"
    }
}
