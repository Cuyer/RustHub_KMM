package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
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
        syncDataSource.setState(ItemSyncState.PENDING)
        return when (val result = repository.getItems()) {
            is DomainResult.Success -> {
                dataSource.upsertItems(result.data)
                syncDataSource.setState(ItemSyncState.DONE)
                Result.success()
            }
            is DomainResult.Error -> Result.retry()
        }
    }
}
