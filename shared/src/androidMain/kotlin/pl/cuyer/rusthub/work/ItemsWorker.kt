package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.domain.repository.item.ItemRepository
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource
import pl.cuyer.rusthub.domain.model.ItemSyncState
import pl.cuyer.rusthub.common.Result as DomainResult

class ItemsWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: ItemRepository,
    private val dataSource: ItemDataSource,
    private val syncDataSource: ItemSyncDataSource
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        var hadError = false
        repository.getItems().collect { result ->
            when (result) {
                is DomainResult.Success -> {
                    dataSource.upsertItems(result.data)
                    syncDataSource.setState(ItemSyncState.PENDING)
                }
                is DomainResult.Error -> {
                    hadError = true
                }
            }
        }

        return if (!hadError) {
            syncDataSource.setState(ItemSyncState.DONE)
            Result.success()
        } else {
            Result.retry()
        }
    }
}