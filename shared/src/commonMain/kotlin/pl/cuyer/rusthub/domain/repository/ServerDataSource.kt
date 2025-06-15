package pl.cuyer.rusthub.domain.repository

import app.cash.paging.PagingSource
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun getPagedServers(query: ServerQuery, limit: Long, offset: Long): List<ServerInfo>
    fun updateMap(id: Long, mapImage: String)
    fun updateFavourite(id: Long, favourite: Boolean)
    fun findKey(key: String): String?
    fun insertOrReplaceRemoteKey(id: String, nextKey: String?)
    fun clearNotFavouriteServers()
    fun clearRemoteKeys()
    fun getServersPagingSource(query: ServerQuery): PagingSource<Int, ServerInfo>
}