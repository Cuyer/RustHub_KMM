package pl.cuyer.rusthub.domain.repository.favourite

import pl.cuyer.rusthub.domain.model.FavouriteSyncOperation

interface FavouriteSyncDataSource {
    suspend fun upsertOperation(operation: FavouriteSyncOperation)
    suspend fun deleteOperation(serverId: Long)
    suspend fun getPendingOperations(): List<FavouriteSyncOperation>
    suspend fun clearOperations()
}
