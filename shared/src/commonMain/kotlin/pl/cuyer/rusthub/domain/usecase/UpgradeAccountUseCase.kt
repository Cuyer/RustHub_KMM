package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.util.MessagingTokenManager
import pl.cuyer.rusthub.util.TokenRefresher

class UpgradeAccountUseCase(
    private val repository: AuthRepository,
    private val dataSource: AuthDataSource,
    private val tokenManager: MessagingTokenManager,
    private val tokenRefresher: TokenRefresher,
) {
    operator fun invoke(username: String, email: String, password: String): Flow<Result<Unit>> = channelFlow {
        repository.upgrade(username, email, password).collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    with(result.data) {
                        dataSource.insertUser(
                            email = email,
                            username = username,
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            obfuscatedId = obfuscatedId,
                            provider = provider,
                            subscribed = subscribed,
                            emailConfirmed = false,
                        )
                        tokenRefresher.clear()
                        tokenManager.currentToken()
                        send(Result.Success(Unit))
                    }
                }

                is Result.Error -> send(Result.Error(result.exception))
            }
        }
    }
}
