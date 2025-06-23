package pl.cuyer.rusthub.domain.model

data class FavouriteSyncOperation(
    val serverId: Long,
    val isAdd: Boolean,
    val syncState: SyncState
)

enum class SyncState {
    PENDING,
    SUCCESS,
    ERROR
}
