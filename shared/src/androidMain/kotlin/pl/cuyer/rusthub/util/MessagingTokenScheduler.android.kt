package pl.cuyer.rusthub.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import pl.cuyer.rusthub.work.TokenRefreshWorker
import java.util.concurrent.TimeUnit

actual class MessagingTokenScheduler(private val context: Context) {
    /**
     * Schedule periodic token refresh if no [TokenRefreshWorker] is currently enqueued or running.
     */
    actual fun schedule() {
        val manager = WorkManager.getInstance(context)
        val existing = manager.getWorkInfosForUniqueWork(TokenRefreshWorker.WORK_NAME).get()
        val active = existing.any { info ->
            info.state == WorkInfo.State.ENQUEUED || info.state == WorkInfo.State.RUNNING
        }
        if (!active) {
            val request = PeriodicWorkRequestBuilder<TokenRefreshWorker>(15, TimeUnit.MINUTES)
                .setConstraints(
                    Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build()
                )
                .build()
            manager.enqueueUniquePeriodicWork(
                TokenRefreshWorker.WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
