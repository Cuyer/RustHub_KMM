package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository

class RequestPasswordResetUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(email: String): Flow<Result<Unit>> =
        repository.requestPasswordReset(email)
}
