package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class SubscriptionSyncOperation(
    val serverId: Long,
    val isAdd: Boolean,
    val syncState: SyncState
)
