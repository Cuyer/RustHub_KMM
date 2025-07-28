package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

class SetSubscribedUseCase(
    private val dataSource: AuthDataSource,
) {
    suspend operator fun invoke(subscribed: Boolean) {
        dataSource.updateSubscribed(subscribed)
    }
}
