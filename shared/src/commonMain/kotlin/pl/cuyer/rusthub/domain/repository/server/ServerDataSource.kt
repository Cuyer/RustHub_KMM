package pl.cuyer.rusthub.domain.repository.server

import app.cash.paging.PagingSource
import database.ServerEntity
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun getServersPagingSource(searchQuery: String?): PagingSource<Int, ServerEntity>
    fun getServerById(serverId: Long): Flow<ServerInfo?>
    fun deleteServers()
}