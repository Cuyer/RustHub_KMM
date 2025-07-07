package pl.cuyer.rusthub.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import pl.cuyer.rusthub.work.SubscriptionSyncWorker

actual class SubscriptionSyncScheduler(
    private val context: Context
) {
    actual fun schedule(serverId: Long) {
        val request = OneTimeWorkRequestBuilder<SubscriptionSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager
            .getInstance(context)
            .enqueueUniqueWork(
                "subscription_$serverId",
                ExistingWorkPolicy.REPLACE,
                request
            )
    }
}
