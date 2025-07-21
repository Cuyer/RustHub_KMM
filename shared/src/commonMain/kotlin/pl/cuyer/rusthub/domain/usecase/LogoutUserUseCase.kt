package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.util.TokenRefresher

class LogoutUserUseCase(
    private val repository: AuthRepository,
    private val dataSource: AuthDataSource
) {
    operator fun invoke(): Flow<Result<Unit>> = channelFlow {
        repository.logout().collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    dataSource.deleteUser()
                    send(Result.Success(Unit))
                }

                is Result.Error -> send(Result.Error(result.exception))
            }
        }
    }
}
