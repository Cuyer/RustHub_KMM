package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

class GetUserUseCase(
    private val dataSource: AuthDataSource,
) {
    operator fun invoke(): Flow<User?> = dataSource.getUser()
}
