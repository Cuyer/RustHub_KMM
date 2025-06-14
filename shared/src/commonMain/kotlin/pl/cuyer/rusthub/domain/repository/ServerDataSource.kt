package pl.cuyer.rusthub.domain.repository

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery

interface ServerDataSource {
    fun upsertServers(servers: List<ServerInfo>)
    fun findServers(query: ServerQuery): Flow<List<ServerInfo>>
}