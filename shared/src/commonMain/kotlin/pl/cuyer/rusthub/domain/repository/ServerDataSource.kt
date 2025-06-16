package pl.cuyer.rusthub.domain.repository

import app.cash.paging.PagingSource
import database.Server
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.remoteKey.RemoteKeyDataSource

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun updateMap(id: Long, mapImage: String)
    fun updateFavourite(id: Long, favourite: Boolean)
    fun clearNotFavouriteServers()
    fun getServersPagingSource(query: ServerQuery): PagingSource<Int, Server>
}