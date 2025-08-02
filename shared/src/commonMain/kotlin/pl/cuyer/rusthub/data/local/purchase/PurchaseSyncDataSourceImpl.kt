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
            safeExecute {
                queries.upsertPurchaseSync(
                    token = operation.token,
                    product_id = operation.productId,
                    sync_state = operation.syncState.name
                )
            }
        }
    }

    override suspend fun deleteOperation(token: String) {
        withContext(Dispatchers.IO) { safeExecute { queries.deletePurchaseSync(token = token) } }
    }

    override suspend fun getPendingOperations(): List<PurchaseSyncOperation> {
        return withContext(Dispatchers.IO) {
            safeQuery(emptyList()) {
                queries.getPendingPurchaseSync()
                    .executeAsList()
                    .map {
                        PurchaseSyncOperation(
                            token = it.token,
                            productId = it.product_id,
                            syncState = SyncState.valueOf(it.sync_state)
                        )
                    }
            }
        }
    }

    override suspend fun clearOperations() {
        withContext(Dispatchers.IO) { safeExecute { queries.clearPurchaseSync() } }
    }
}
