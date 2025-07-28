package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.domain.repository.user.UserRepository
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.common.Result as DomainResult

class UserSyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: UserRepository,
    private val authDataSource: AuthDataSource
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        var workResult: Result = Result.success()
        repository.getUser().collectLatest { result ->
            when (result) {
                is DomainResult.Success -> {
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
                    workResult = Result.success()
                }
                is DomainResult.Error -> {
                    CrashReporter.recordException(result.exception)
                    workResult = Result.retry()
                }
            }
        }
        return workResult
    }
}
