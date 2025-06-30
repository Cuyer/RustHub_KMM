package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository
import pl.cuyer.rusthub.common.Result as DomainResult

class DeleteAccountWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: AuthRepository
) : CoroutineWorker(appContext, params) {

    companion object {
        const val WORK_NAME = "delete_account"
        const val USERNAME = "username"
        const val PASSWORD = "password"
    }

    override suspend fun doWork(): Result {
        val username = inputData.getString(USERNAME) ?: return Result.failure()
        val password = inputData.getString(PASSWORD) ?: return Result.failure()

        var workResult: Result = Result.retry()
        repository.deleteAccount(username, password).collectLatest { result ->
            workResult = when (result) {
                is DomainResult.Success -> Result.success()
                is DomainResult.Error -> Result.failure()
                else -> workResult
            }
        }
        return workResult
    }
}
