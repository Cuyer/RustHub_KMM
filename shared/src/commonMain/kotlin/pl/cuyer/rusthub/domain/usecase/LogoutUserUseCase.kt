package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.util.TokenRefresher
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.CancellationException

class LogoutUserUseCase(
    private val repository: AuthRepository,
    private val dataSource: AuthDataSource
) {
    operator fun invoke(): Flow<Result<Unit>> = channelFlow {
        repository.logout().collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    try {
                        dataSource.deleteUser()
                        send(Result.Success(Unit))
                    } catch (e: Exception) {
                        if (e is CancellationException) throw e
                        CrashReporter.recordException(e)
                        send(Result.Error(e))
                    }
                }

                is Result.Error -> send(Result.Error(result.exception))
            }
        }
    }
}
