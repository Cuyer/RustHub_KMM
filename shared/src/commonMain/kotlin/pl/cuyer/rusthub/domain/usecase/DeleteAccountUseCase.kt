package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.util.TokenRefresher

class DeleteAccountUseCase(
    private val repository: AuthRepository,
    private val dataSource: AuthDataSource,
    private val tokenRefresher: TokenRefresher,
) {
    operator fun invoke(password: String): Flow<Result<Unit>> = channelFlow {
        repository.deleteAccount(password).collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    dataSource.deleteUser()
                    tokenRefresher.clear()
                    send(Result.Success(Unit))
                }

                is Result.Error -> send(Result.Error(result.exception))

                is Result.Loading -> send(Result.Loading)
            }
        }
    }
}
