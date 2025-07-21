package pl.cuyer.rusthub.data.local.item

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.ItemSyncState
import pl.cuyer.rusthub.domain.repository.item.local.ItemSyncDataSource

class ItemSyncDataSourceImpl(db: RustHubDatabase) : ItemSyncDataSource, Queries(db) {
    override suspend fun setState(state: ItemSyncState) {
        withContext(Dispatchers.IO) {
            queries.upsertItemSync(id = Constants.DEFAULT_KEY, sync_state = state.name)
        }
    }

    override fun observeState(): Flow<ItemSyncState?> {
        return queries.getItemSync(Constants.DEFAULT_KEY)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.sync_state?.let { value -> ItemSyncState.valueOf(value) } }
    }
}
