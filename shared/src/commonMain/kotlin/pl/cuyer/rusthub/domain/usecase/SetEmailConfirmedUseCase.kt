package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

class SetEmailConfirmedUseCase(
    private val dataSource: AuthDataSource,
) {
    suspend operator fun invoke(confirmed: Boolean) {
        dataSource.updateEmailConfirmed(confirmed)
    }
}
