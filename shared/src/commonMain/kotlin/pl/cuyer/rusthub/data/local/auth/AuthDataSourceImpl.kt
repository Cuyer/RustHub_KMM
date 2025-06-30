package pl.cuyer.rusthub.data.local.auth

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toUser
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

class AuthDataSourceImpl(
    private val db: RustHubDatabase
) : AuthDataSource, Queries(db) {

    override suspend fun insertUser(
        email: String?,
        username: String,
        accessToken: String,
        refreshToken: String?,
        provider: AuthProvider,
    ) {
        withContext(Dispatchers.IO) {
            queries.insertUser(
                id = Constants.DEFAULT_KEY,
                email = email,
                username = username,
                accessToken = accessToken,
                refreshToken = refreshToken,
                provider = provider.name
            )
        }
    }

    override suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            queries.deleteUser()
        }
    }

    override fun getUser(): Flow<User?> {
        return queries.getUser()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toUser() }
    }

    override suspend fun getUserOnce(): User? {
        return withContext(Dispatchers.IO) {
            queries.getUser().executeAsOneOrNull()?.toUser()
        }
    }

}