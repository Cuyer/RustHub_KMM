package pl.cuyer.rusthub.domain.repository.subscription

import pl.cuyer.rusthub.domain.model.SubscriptionSyncOperation

interface SubscriptionSyncDataSource {
    suspend fun upsertOperation(operation: SubscriptionSyncOperation)
    suspend fun deleteOperation(serverId: Long)
    suspend fun getPendingOperations(): List<SubscriptionSyncOperation>
    suspend fun clearOperations()
}
