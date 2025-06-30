package pl.cuyer.rusthub.domain.repository.auth

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.AuthProvider

interface AuthDataSource {
    suspend fun insertUser(
        email: String?,
        username: String,
        accessToken: String,
        refreshToken: String?,
        provider: AuthProvider,
    )

    suspend fun deleteUser()
    fun getUser(): Flow<User?>

    suspend fun getUserOnce(): User?
}