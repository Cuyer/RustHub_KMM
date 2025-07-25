package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource

class ClearRemoteKeysUseCase(
    private val remoteKeys: RemoteKeyDataSource
) {
    suspend operator fun invoke() {
        remoteKeys.clearKeys()
    }
}
