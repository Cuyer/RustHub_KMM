package pl.cuyer.rusthub.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import pl.cuyer.rusthub.work.MonumentsWorker
import java.util.concurrent.TimeUnit

actual class MonumentsScheduler(private val context: Context) {
    actual fun schedule() {
        val request = PeriodicWorkRequestBuilder<MonumentsWorker>(7, TimeUnit.DAYS)
            .setInitialDelay(7, TimeUnit.DAYS)
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            WORK_NAME_PERIODIC,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    actual fun startNow() {
        val request = OneTimeWorkRequestBuilder<MonumentsWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }

    companion object {
        private const val WORK_NAME = "monuments_sync"
        private const val WORK_NAME_PERIODIC = "monuments_sync_periodic"
    }
}
