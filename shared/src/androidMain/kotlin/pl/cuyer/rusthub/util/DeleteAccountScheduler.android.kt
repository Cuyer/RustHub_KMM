package pl.cuyer.rusthub.util

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import pl.cuyer.rusthub.work.DeleteAccountWorker

actual class DeleteAccountScheduler(private val context: Context) {
    actual fun schedule(username: String, password: String) {
        val request = OneTimeWorkRequestBuilder<DeleteAccountWorker>()
            .setInputData(
                workDataOf(
                    DeleteAccountWorker.USERNAME to username,
                    DeleteAccountWorker.PASSWORD to password
                )
            )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()
        WorkManager.getInstance(context).enqueueUniqueWork(
            DeleteAccountWorker.WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            request
        )
    }
}
