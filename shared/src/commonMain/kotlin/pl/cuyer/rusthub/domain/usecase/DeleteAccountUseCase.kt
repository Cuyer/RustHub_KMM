package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.DeleteAccountScheduler

class DeleteAccountUseCase(
    private val dataSource: AuthDataSource,
    private val scheduler: DeleteAccountScheduler
) {
    suspend operator fun invoke(password: String) {
        dataSource.deleteUser()
        scheduler.schedule(password)
    }
}
