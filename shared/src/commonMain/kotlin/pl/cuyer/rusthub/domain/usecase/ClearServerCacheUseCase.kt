package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource

class ClearServerCacheUseCase(
    private val cacheDataSource: ServerCacheDataSource
) {
    suspend operator fun invoke() {
        cacheDataSource.clearServers()
    }
}
