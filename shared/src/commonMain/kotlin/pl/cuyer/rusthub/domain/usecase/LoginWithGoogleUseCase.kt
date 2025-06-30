package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.util.MessagingTokenManager

class LoginWithGoogleUseCase(
    private val client: AuthRepository,
    private val dataSource: AuthDataSource,
    private val tokenManager: MessagingTokenManager,
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(token: String): Flow<Result<Unit>> = channelFlow {
        client.loginWithGoogle(token).collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    with(result.data) {
                        dataSource.insertUser(
                            email = email,
                            username = username,
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            provider = provider
                        )
                        tokenManager.currentToken()
                        send(Result.Success(Unit))
                    }
                }
                is Result.Error -> send(Result.Error(result.exception))
                is Result.Loading -> send(Result.Loading)
            }
        }
    }
}
