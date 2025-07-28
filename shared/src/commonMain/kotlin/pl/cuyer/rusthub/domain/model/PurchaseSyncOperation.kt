package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class PurchaseSyncOperation(
    val token: String,
    val productId: String?,
    val syncState: SyncState
)
