package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource

class ClearServersAndKeysUseCase(
    private val cacheDataSource: ServerCacheDataSource
) {
    suspend operator fun invoke() {
        cacheDataSource.clearServersAndKeys()
    }
}
