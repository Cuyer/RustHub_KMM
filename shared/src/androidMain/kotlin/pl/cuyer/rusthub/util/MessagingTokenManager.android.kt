package pl.cuyer.rusthub.util

import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository

actual class MessagingTokenManager actual constructor(
    private val repository: MessagingTokenRepository,
    private val scheduler: MessagingTokenScheduler
) {
    /**
     * Returns the current FCM token and ensures the refresh worker is scheduled.
     *
     * The scheduler checks for existing [TokenRefreshWorker] instances so
     * invoking this method repeatedly will not reset the worker's period.
     */
    actual suspend fun currentToken(): String? {
        val token = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()
        token?.let {
            repository.registerToken(it, Clock.System.now())
            scheduler.schedule()
        }
        return token
    }

    actual suspend fun deleteToken() {
        val token = runCatching { FirebaseMessaging.getInstance().token.await() }.getOrNull()
        token?.let { repository.deleteToken(it) }
        runCatching { FirebaseMessaging.getInstance().deleteToken().await() }
    }
}
