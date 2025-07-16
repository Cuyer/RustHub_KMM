package pl.cuyer.rusthub.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import pl.cuyer.rusthub.domain.model.NotificationType
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.util.NotificationPresenter

class RustHubFirebaseMessagingService : FirebaseMessagingService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val tokenManager by inject<MessagingTokenManager>()
    private val authDataSource by inject<AuthDataSource>()

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
        serviceScope.launch {
            if (authDataSource.getUserOnce() != null) {
                tokenManager.registerToken(token)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
    }
}