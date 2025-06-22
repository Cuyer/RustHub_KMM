package pl.cuyer.rusthub.domain.repository.auth

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.User

interface AuthDataSource {
    suspend fun insertUser(
        email: String,
        username: String,
        accessToken: String,
        refreshToken: String
    )

    suspend fun deleteUser()
    fun getUser(): Flow<User?>
}