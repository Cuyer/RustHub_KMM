package pl.cuyer.rusthub.util

import com.google.firebase.messaging.FirebaseMessaging
import io.github.aakira.napier.Napier
import kotlinx.coroutines.tasks.await
import kotlinx.datetime.Clock
import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository

actual class MessagingTokenManager actual constructor(
    private val repository: MessagingTokenRepository,
    private val scheduler: MessagingTokenScheduler
) {
    actual suspend fun registerToken(token: String) {
        repository.registerToken(token, Clock.System.now())
        scheduler.schedule()
    }
    /**
     * Returns the current FCM token and ensures the refresh worker is scheduled.
     *
     * The scheduler checks for existing [TokenRefreshWorker] instances so
     * invoking this method repeatedly will not reset the worker's period.
     */
    actual suspend fun currentToken(): String? {
        return try {
            val token = FirebaseMessaging.getInstance().token.await()
            repository.registerToken(token, Clock.System.now())
            scheduler.schedule()
            token
        } catch (e: Exception) {
            Napier.e("Failed to get FCM token", e)
            null
        }
    }

    actual suspend fun deleteToken() {
        val token = try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Napier.e("Failed to read FCM token before deletion", e)
            null
        }
        token?.let { repository.deleteToken(it) }
        try {
            FirebaseMessaging.getInstance().deleteToken().await()
        } catch (e: Exception) {
            Napier.e("Failed to delete FCM token", e)
        }
    }
}
