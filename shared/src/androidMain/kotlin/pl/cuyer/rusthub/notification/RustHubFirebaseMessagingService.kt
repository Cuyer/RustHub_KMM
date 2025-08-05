package pl.cuyer.rusthub.notification

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import pl.cuyer.rusthub.domain.model.NotificationType
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.presentation.di.RustHubApplication
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.util.NotificationPresenter

class RustHubFirebaseMessagingService : FirebaseMessagingService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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
        val app = application as RustHubApplication
        serviceScope.launch {
            app.koinReady.await()
            val tokenManager: MessagingTokenManager = get()
            val authDataSource: AuthDataSource = get()
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