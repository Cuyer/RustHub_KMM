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
    override suspend fun doWork(): Result {
        tokenManager.currentToken()
        return Result.success()
    }
}
