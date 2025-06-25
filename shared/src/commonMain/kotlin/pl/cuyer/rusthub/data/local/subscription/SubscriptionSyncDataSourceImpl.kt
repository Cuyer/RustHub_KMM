package pl.cuyer.rusthub.data.local.subscription

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.SubscriptionSyncOperation
import pl.cuyer.rusthub.domain.model.SyncState
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource

class SubscriptionSyncDataSourceImpl(
    db: RustHubDatabase
) : SubscriptionSyncDataSource, Queries(db) {
    override suspend fun upsertOperation(operation: SubscriptionSyncOperation) {
        withContext(Dispatchers.IO) {
            queries.upsertSubscriptionSync(
                server_id = operation.serverId,
                action = if (operation.isAdd) 1L else 0L,
                sync_state = operation.syncState.name
            )
        }
    }

    override suspend fun deleteOperation(serverId: Long) {
        withContext(Dispatchers.IO) {
            queries.deleteSubscriptionSync(server_id = serverId)
        }
    }

    override suspend fun getPendingOperations(): List<SubscriptionSyncOperation> {
        return withContext(Dispatchers.IO) {
            queries.getPendingSubscriptionSync()
                .executeAsList()
                .map {
                    SubscriptionSyncOperation(
                        serverId = it.server_id,
                        isAdd = it.action == 1L,
                        syncState = SyncState.valueOf(it.sync_state)
                    )
                }
        }
    }
}
