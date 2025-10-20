package pl.cuyer.rusthub.data.local.auth

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toUser
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.repository.favourite.FavouriteSyncDataSource
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.search.ItemSearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.search.MonumentSearchQueryDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.subscription.SubscriptionSyncDataSource
import pl.cuyer.rusthub.util.CrashReporter
import pl.cuyer.rusthub.util.TokenRefresher

class AuthDataSourceImpl(
    private val db: RustHubDatabase,
    val tokenRefresher: TokenRefresher,
    private val serverDataSource: ServerDataSource,
    private val filtersDataSource: FiltersDataSource,
    private val filtersOptionsDataSource: FiltersOptionsDataSource,
    private val searchQueryDataSource: SearchQueryDataSource,
    private val itemSearchQueryDataSource: ItemSearchQueryDataSource,
    private val monumentSearchQueryDataSource: MonumentSearchQueryDataSource,
    private val favouriteSyncDataSource: FavouriteSyncDataSource,
    private val subscriptionSyncDataSource: SubscriptionSyncDataSource,
) : AuthDataSource, Queries(db) {

    override suspend fun insertUser(
        email: String?,
        username: String,
        accessToken: String,
        refreshToken: String?,
        obfuscatedId: String?,
        provider: AuthProvider,
        subscribed: Boolean,
        emailConfirmed: Boolean,
    ) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.insertUser(
                    id = Constants.DEFAULT_KEY,
                    email = email,
                    username = username,
                    accessToken = accessToken,
                    refreshToken = refreshToken,
                    obfuscatedId = obfuscatedId,
                    provider = provider.name,
                    subscribed = if (subscribed) 1L else 0L,
                    emailConfirmed = if (emailConfirmed) 1L else 0L
                )
            }
            CrashReporter.setUserId(username)
        }
    }

    override suspend fun deleteUser() {
        withContext(Dispatchers.IO) {
            safeExecute { queries.deleteUser() }
            serverDataSource.deleteServers()
            filtersDataSource.clearFilters()
            filtersOptionsDataSource.clearFiltersOptions()
            searchQueryDataSource.clearQueries()
            itemSearchQueryDataSource.clearQueries()
            monumentSearchQueryDataSource.clearQueries()
            favouriteSyncDataSource.clearOperations()
            subscriptionSyncDataSource.clearOperations()
            tokenRefresher.clear()
            CrashReporter.setUserId(null)
        }
    }

    override fun getUser(): Flow<User?> {
        return queries.getUser()
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.toUser() }
            .flowOn(Dispatchers.Default)
            .catch { e ->
                CrashReporter.recordException(e)
                throw e
            }
    }

    override suspend fun getUserOnce(): User? {
        return withContext(Dispatchers.IO) { safeQuery(null) { queries.getUser().executeAsOneOrNull()?.toUser() } }
    }

    override suspend fun updateEmailConfirmed(confirmed: Boolean) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.updateEmailConfirmed(
                    id = Constants.DEFAULT_KEY,
                    confirmed = confirmed
                )
            }
        }
    }

    override suspend fun updateSubscribed(subscribed: Boolean) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.updateSubscribed(
                    id = Constants.DEFAULT_KEY,
                    subscribed = subscribed
                )
            }
        }
    }

}