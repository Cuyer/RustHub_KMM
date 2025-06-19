package pl.cuyer.rusthub.domain.model

/**
 * Represents paging keys used by [ServerRemoteMediator].
 */
data class RemoteKey(
    val id: String,
    val nextPage: Long?,
    val lastUpdated: Long
)
