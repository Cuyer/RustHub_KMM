package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import pl.cuyer.rusthub.util.MessagingTokenManager

class TokenRefreshWorker(
    appContext: Context,
    params: WorkerParameters,
    private val tokenManager: MessagingTokenManager
) : CoroutineWorker(appContext, params) {
    companion object {
        const val WORK_NAME = "token_refresh"
    }

    override suspend fun doWork(): Result {
        // This will fetch the token and schedule the refresh worker if needed.
        // Scheduling is idempotent so the worker won't reset its period on each run.
        tokenManager.currentToken()
        return Result.success()
    }
}
