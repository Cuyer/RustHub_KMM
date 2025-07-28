package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.user.UserRepository
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.UserSyncScheduler
import pl.cuyer.rusthub.util.CrashReporter

class RefreshUserUseCase(
    private val repository: UserRepository,
    private val authDataSource: AuthDataSource,
    private val scheduler: UserSyncScheduler
) {
    operator fun invoke(): Flow<Result<Unit>> = channelFlow {
        repository.getUser().collectLatest { result ->
            when (result) {
                is Result.Success -> {
                    val current = authDataSource.getUserOnce()
                    if (current != null) {
                        val user = result.data
                        authDataSource.insertUser(
                            email = user.email,
                            username = user.username,
                            accessToken = current.accessToken,
                            refreshToken = current.refreshToken,
                            obfuscatedId = user.obfuscatedId ?: current.obfuscatedId,
                            provider = user.provider,
                            subscribed = user.subscribed,
                            emailConfirmed = user.emailConfirmed
                        )
                    }
                    send(Result.Success(Unit))
                }
                is Result.Error -> {
                    CrashReporter.recordException(result.exception)
                    scheduler.schedule()
                    send(Result.Error(result.exception))
                }
            }
        }
    }
}
