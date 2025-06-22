package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

class LogoutUserUseCase(
    private val dataSource: AuthDataSource
) {
    suspend operator fun invoke() {
        dataSource.deleteUser()
    }
}
