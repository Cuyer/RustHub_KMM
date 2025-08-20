package pl.cuyer.rusthub.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.flow.collectLatest
import pl.cuyer.rusthub.domain.model.MonumentSyncState
import pl.cuyer.rusthub.domain.repository.monument.MonumentRepository
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentSyncDataSource
import pl.cuyer.rusthub.common.Result as DomainResult

class MonumentsWorker(
    appContext: Context,
    params: WorkerParameters,
    private val repository: MonumentRepository,
    private val dataSource: MonumentDataSource,
    private val syncDataSource: MonumentSyncDataSource
) : CoroutineWorker(appContext, params) {

    override suspend fun doWork(): Result {
        var workResult: Result = Result.success()
        repository.getMonuments().collectLatest { result ->
            when (result) {
                is DomainResult.Success -> {
                    dataSource.upsertMonuments(result.data)
                    syncDataSource.setState(MonumentSyncState.DONE)
                    workResult = Result.success()
                }
                is DomainResult.Error -> {
                    workResult = Result.retry()
                }
            }
        }
        return workResult
    }
}
