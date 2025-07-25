package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

@Immutable
data class FavouriteSyncOperation(
    val serverId: Long,
    val isAdd: Boolean,
    val syncState: SyncState
)

@Immutable
enum class SyncState {
    PENDING
}
