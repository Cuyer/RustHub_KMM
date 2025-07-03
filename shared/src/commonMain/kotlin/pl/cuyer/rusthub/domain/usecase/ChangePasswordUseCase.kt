package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository

class ChangePasswordUseCase(
    private val repository: AuthRepository
) {
    operator fun invoke(oldPassword: String, newPassword: String): Flow<Result<Unit>> =
        repository.changePassword(oldPassword, newPassword)
}
