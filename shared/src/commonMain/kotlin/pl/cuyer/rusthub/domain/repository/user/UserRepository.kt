package pl.cuyer.rusthub.domain.repository.user

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.UserStatus

interface UserRepository {
    fun getUserStatus(): Flow<Result<UserStatus>>
    fun resendConfirmation(): Flow<Result<Unit>>
    fun getUser(): Flow<Result<User>>
}
