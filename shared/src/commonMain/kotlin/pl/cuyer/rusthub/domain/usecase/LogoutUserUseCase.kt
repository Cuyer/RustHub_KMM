package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.LogoutScheduler
import pl.cuyer.rusthub.util.TokenRefresher

class LogoutUserUseCase(
    private val dataSource: AuthDataSource,
    private val scheduler: LogoutScheduler,
    private val tokenRefresher: TokenRefresher,
) {
    suspend operator fun invoke() {
        dataSource.deleteUser()
        tokenRefresher.clear()
        scheduler.schedule()
    }
}
