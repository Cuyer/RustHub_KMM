package pl.cuyer.rusthub.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pl.cuyer.rusthub.util.NotificationPresenter

class RustHubFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        val title = message.notification?.title ?: return
        val body = message.notification?.body ?: return
        NotificationPresenter(this).show(title, body)
    }
}
