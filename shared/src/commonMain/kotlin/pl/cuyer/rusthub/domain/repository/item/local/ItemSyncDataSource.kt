package pl.cuyer.rusthub.domain.repository.item.local

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ItemSyncState

interface ItemSyncDataSource {
    suspend fun setState(state: ItemSyncState)
    fun observeState(): Flow<ItemSyncState?>
    suspend fun clearState()
}
