package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.server.ServerDataSource

class ClearServerCacheUseCase(
    private val dataSource: ServerDataSource
) {
    suspend operator fun invoke() {
        dataSource.clearServersAndKeys()
    }
}
