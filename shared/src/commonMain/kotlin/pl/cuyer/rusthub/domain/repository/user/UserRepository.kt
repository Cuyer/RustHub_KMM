package pl.cuyer.rusthub.domain.repository.user

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.User

interface UserRepository {
    fun isEmailConfirmed(): Flow<Result<Boolean>>
    fun resendConfirmation(): Flow<Result<Unit>>
    fun getUser(): Flow<Result<User>>
}
