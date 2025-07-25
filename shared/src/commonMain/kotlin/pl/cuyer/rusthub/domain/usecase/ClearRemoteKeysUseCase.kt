package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource

class ClearRemoteKeysUseCase(
    private val dataSource: RemoteKeyDataSource
) {
    suspend operator fun invoke() {
        dataSource.clearKeys()
    }
}
