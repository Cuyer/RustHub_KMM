package pl.cuyer.rusthub.domain.repository

import app.cash.paging.PagingSource
import database.ServerEntity
import pl.cuyer.rusthub.domain.model.ServerInfo

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun getServersPagingSource(): PagingSource<Int, ServerEntity>
    fun deleteServers()
}