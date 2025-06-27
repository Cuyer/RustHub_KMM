package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.MessagingTokenManager

class LogoutUserUseCase(
    private val dataSource: AuthDataSource,
    private val tokenManager: MessagingTokenManager,
) {
    suspend operator fun invoke() {
        tokenManager.deleteToken()
        dataSource.deleteUser()
    }
}