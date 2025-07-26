package pl.cuyer.rusthub.data.local.purchase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.PurchaseSyncOperation
import pl.cuyer.rusthub.domain.model.SyncState
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseSyncDataSource

class PurchaseSyncDataSourceImpl(
    db: RustHubDatabase
) : PurchaseSyncDataSource, Queries(db) {
    override suspend fun upsertOperation(operation: PurchaseSyncOperation) {
        withContext(Dispatchers.IO) {
            queries.upsertPurchaseSync(
                token = operation.token,
                sync_state = operation.syncState.name
            )
        }
    }

    override suspend fun deleteOperation(token: String) {
        withContext(Dispatchers.IO) { queries.deletePurchaseSync(token = token) }
    }

    override suspend fun getPendingOperations(): List<PurchaseSyncOperation> {
        return withContext(Dispatchers.IO) {
            queries.getPendingPurchaseSync()
                .executeAsList()
                .map {
                    PurchaseSyncOperation(
                        token = it.token,
                        syncState = SyncState.valueOf(it.sync_state)
                    )
                }
        }
    }

    override suspend fun clearOperations() {
        withContext(Dispatchers.IO) { queries.clearPurchaseSync() }
    }
}
