package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class GetServerDetailsUseCase(
    private val dataSource: ServerDataSource
) {
    operator fun invoke(serverId: Long): Flow<ServerInfo?> {
        return dataSource.getServerById(serverId)
    }
}