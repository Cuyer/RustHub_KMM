package pl.cuyer.rusthub.domain.repository.auth

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.AccessToken
import pl.cuyer.rusthub.domain.model.TokenPair
import pl.cuyer.rusthub.domain.model.UserExistsInfo

interface AuthRepository {
    fun register(email: String, password: String, username: String): Flow<Result<TokenPair>>
    fun login(username: String, password: String): Flow<Result<TokenPair>>
    fun refresh(refreshToken: String): Flow<Result<TokenPair>>
    fun upgrade(email: String, username: String, password: String): Flow<Result<TokenPair>>
    fun authAnonymously(): Flow<Result<AccessToken>>
    fun loginWithGoogle(token: String): Flow<Result<TokenPair>>
    fun logout(): Flow<Result<Unit>>
    fun deleteAccount(username: String, password: String): Flow<Result<Unit>>
    fun checkUserExists(email: String): Flow<Result<UserExistsInfo>>
}
