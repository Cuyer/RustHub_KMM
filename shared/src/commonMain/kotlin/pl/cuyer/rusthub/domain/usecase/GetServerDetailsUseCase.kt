package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class GetServerDetailsUseCase(
    private val dataSource: ServerDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(serverId: Long): Flow<ServerInfo?> {
        return dataSource.getServerById(serverId)
    }
}