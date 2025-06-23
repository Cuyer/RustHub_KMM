package pl.cuyer.rusthub.util

import android.content.Context
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.NetworkType
import androidx.work.Constraints
import pl.cuyer.rusthub.work.FavouriteSyncWorker

actual class SyncScheduler(private val context: Context) {
    actual fun schedule() {
        val request = OneTimeWorkRequestBuilder<FavouriteSyncWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueue(request)
    }
}
