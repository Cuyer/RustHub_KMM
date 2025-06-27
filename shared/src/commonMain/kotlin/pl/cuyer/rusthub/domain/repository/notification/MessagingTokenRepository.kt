package pl.cuyer.rusthub.domain.repository.notification

import kotlinx.datetime.Instant

interface MessagingTokenRepository {
    suspend fun registerToken(token: String, timestamp: Instant)
    suspend fun deleteToken(token: String)
}
