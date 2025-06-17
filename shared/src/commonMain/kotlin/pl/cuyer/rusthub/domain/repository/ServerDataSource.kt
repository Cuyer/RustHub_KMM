package pl.cuyer.rusthub.domain.repository

import app.cash.paging.PagingSource
import database.Server
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun getServersPagingSource(query: ServerQuery): PagingSource<Int, Server>
}