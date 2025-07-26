package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource
import pl.cuyer.rusthub.common.Result as DomainResult

class PurchaseSyncWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: PurchaseRepository,
    private val syncDataSource: PurchaseSyncDataSource
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result = coroutineScope {
        val operations = syncDataSource.getPendingOperations()
        if (operations.isEmpty()) return@coroutineScope Result.success()

        val tasks = operations.map { operation ->
            async {
                var success = false
                repository.confirmPurchase(operation.token).collectLatest { result ->
                    when (result) {
                        is DomainResult.Success -> {
                            syncDataSource.deleteOperation(operation.token)
                            success = true
                        }
                        is DomainResult.Error -> {
                            success = false
                        }
                    }
                }
                success
            }
        }

        val results = tasks.awaitAll()
        return@coroutineScope if (results.all { it }) Result.success() else Result.retry()
    }
}
