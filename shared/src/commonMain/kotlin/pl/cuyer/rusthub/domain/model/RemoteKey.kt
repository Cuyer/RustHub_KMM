package pl.cuyer.rusthub.domain.model

import androidx.compose.runtime.Immutable

/**
 * Represents paging keys used by [ServerRemoteMediator].
 */
@Immutable
data class RemoteKey(
    val id: String,
    val nextPage: Long?,
    val lastUpdated: Long
)
