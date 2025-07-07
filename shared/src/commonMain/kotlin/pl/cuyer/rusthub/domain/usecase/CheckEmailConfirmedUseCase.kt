package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.user.UserRepository

class CheckEmailConfirmedUseCase(
    private val repository: UserRepository,
) {
    operator fun invoke(): Flow<Result<Boolean>> = repository.isEmailConfirmed()
}
