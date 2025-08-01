package pl.cuyer.rusthub.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import pl.cuyer.rusthub.work.TokenRefreshWorker
import java.util.concurrent.TimeUnit

actual class MessagingTokenScheduler(private val context: Context) {
    /**
     * Schedule periodic token refresh if no [TokenRefreshWorker] is currently enqueued or running.
     */
    actual fun schedule() {
        val request = PeriodicWorkRequestBuilder<TokenRefreshWorker>(30, TimeUnit.DAYS)
            .setInitialDelay(30, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            TokenRefreshWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }
}
