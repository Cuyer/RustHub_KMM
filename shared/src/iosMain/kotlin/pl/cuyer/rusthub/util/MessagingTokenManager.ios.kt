package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository

actual class MessagingTokenManager(
    private val repository: MessagingTokenRepository,
    private val scheduler: MessagingTokenScheduler
) {
    actual suspend fun registerToken(token: String) {}
    actual suspend fun currentToken(): String? = null
    actual suspend fun deleteToken() {}
}
