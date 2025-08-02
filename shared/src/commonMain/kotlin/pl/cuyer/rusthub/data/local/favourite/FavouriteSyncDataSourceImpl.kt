package pl.cuyer.rusthub.data.local.favourite

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.FavouriteSyncOperation
import pl.cuyer.rusthub.domain.model.SyncState
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource

class FavouriteSyncDataSourceImpl(
    db: RustHubDatabase
) : FavouriteSyncDataSource, Queries(db) {
    override suspend fun upsertOperation(operation: FavouriteSyncOperation) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.upsertFavouriteSync(
                    server_id = operation.serverId,
                    action = if (operation.isAdd) 1L else 0L,
                    sync_state = operation.syncState.name
                )
            }
        }
    }

    override suspend fun deleteOperation(serverId: Long) {
        withContext(Dispatchers.IO) { safeExecute { queries.deleteFavouriteSync(server_id = serverId) } }
    }

    override suspend fun getPendingOperations(): List<FavouriteSyncOperation> {
        return withContext(Dispatchers.IO) {
            safeQuery(emptyList()) {
                queries.getPendingFavouriteSync()
                    .executeAsList()
                    .map {
                        FavouriteSyncOperation(
                            serverId = it.server_id,
                            isAdd = it.action == 1L,
                            syncState = SyncState.valueOf(it.sync_state)
                        )
                    }
            }
        }
    }

    override suspend fun clearOperations() {
        withContext(Dispatchers.IO) { safeExecute { queries.clearFavouriteSync() } }
    }
}
