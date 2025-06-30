package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.LogoutScheduler

class LogoutUserUseCase(
    private val dataSource: AuthDataSource,
    private val scheduler: LogoutScheduler
) {
    suspend operator fun invoke() {
        dataSource.deleteUser()
        scheduler.schedule()
    }
}