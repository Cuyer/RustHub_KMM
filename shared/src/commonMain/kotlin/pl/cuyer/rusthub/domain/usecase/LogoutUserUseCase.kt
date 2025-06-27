package pl.cuyer.rusthub.domain.usecase

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.MessagingTokenManager

class LogoutUserUseCase(
    private val dataSource: AuthDataSource,
    private val tokenManager: MessagingTokenManager,
    private val httpClient: HttpClient,
) {
    suspend operator fun invoke() {
        tokenManager.deleteToken()
        dataSource.deleteUser()
        httpClient.plugin(Auth)
            .providers
            .filterIsInstance<BearerAuthProvider>()
            .forEach { it.clearToken() }
    }
}