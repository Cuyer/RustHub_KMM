package pl.cuyer.rusthub.domain.repository.notification

interface MessagingTokenRepository {
    suspend fun registerToken(token: String)
}
