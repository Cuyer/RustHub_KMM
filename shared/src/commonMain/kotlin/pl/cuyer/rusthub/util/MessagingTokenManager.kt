package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository

/** Provides the current FCM token and allows deleting it. */
expect class MessagingTokenManager(
    repository: MessagingTokenRepository,
    scheduler: MessagingTokenScheduler
) {
    suspend fun currentToken(): String?
    suspend fun deleteToken()
}
