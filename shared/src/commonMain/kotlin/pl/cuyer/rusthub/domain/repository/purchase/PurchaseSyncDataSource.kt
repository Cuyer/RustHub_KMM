package pl.cuyer.rusthub.domain.repository.purchase

import pl.cuyer.rusthub.domain.model.PurchaseSyncOperation

interface PurchaseSyncDataSource {
    suspend fun upsertOperation(operation: PurchaseSyncOperation)
    suspend fun deleteOperation(token: String)
    suspend fun getPendingOperations(): List<PurchaseSyncOperation>
    suspend fun clearOperations()
}
