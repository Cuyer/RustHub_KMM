package pl.cuyer.rusthub.domain.repository.monument.local

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.MonumentSyncState

interface MonumentSyncDataSource {
    suspend fun setState(state: MonumentSyncState)
    fun observeState(): Flow<MonumentSyncState?>
    suspend fun clearState()
}
