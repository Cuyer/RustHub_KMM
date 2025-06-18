package pl.cuyer.rusthub.domain.repository.server

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.PagedServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery

interface ServerRepository {
    fun getServers(page: Int, size: Int, query: ServerQuery): Flow<Result<PagedServerInfo>>
}