package pl.cuyer.rusthub.domain.repository.server

import app.cash.paging.PagingSource
import database.ServerEntity
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.RemoteKey

interface ServerDataSource {
    suspend fun upsertServers(servers: List<ServerInfo>)
    fun getServersPagingSource(
        searchQuery: String?
    ): PagingSource<Int, ServerEntity>
    fun getServerById(serverId: Long): Flow<ServerInfo?>
    suspend fun deleteServers()
    suspend fun updateFavourite(serverId: Long, favourite: Boolean)
    suspend fun updateSubscription(serverId: Long, subscribed: Boolean)
    /**
     * Clears cached servers and paging keys in a single database transaction.
     */
    suspend fun clearServersAndKeys()

    /**
     * Replaces cached servers and keys atomically on refresh.
     */
    suspend fun replaceServersAndKeys(
        servers: List<ServerInfo>,
        key: RemoteKey
    )
}