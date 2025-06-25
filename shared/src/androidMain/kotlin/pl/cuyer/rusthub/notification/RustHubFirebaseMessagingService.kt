package pl.cuyer.rusthub.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.cuyer.rusthub.domain.model.NotificationType
import pl.cuyer.rusthub.util.NotificationPresenter

class RustHubFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val type = message.data["type"]?.let { value ->
            runCatching { NotificationType.valueOf(value) }.getOrNull()
        } ?: return
        val name = message.data["name"] ?: return
        val timestamp = message.data["timestamp"] ?: return
        NotificationPresenter(this).show(name, type, timestamp)
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}
