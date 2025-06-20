package pl.cuyer.rusthub.domain.repository.server

import app.cash.paging.PagingSource
import database.ServerEntity
import pl.cuyer.rusthub.domain.model.ServerInfo

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun getServersPagingSource(searchQuery: String?): PagingSource<Int, ServerEntity>
    fun deleteServers()
}