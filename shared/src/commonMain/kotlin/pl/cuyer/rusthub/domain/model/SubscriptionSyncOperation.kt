package pl.cuyer.rusthub.domain.model

data class SubscriptionSyncOperation(
    val serverId: Long,
    val isAdd: Boolean,
    val syncState: SyncState
)
