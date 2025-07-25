package pl.cuyer.rusthub.data.local.cache

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource

class ServerCacheDataSourceImpl(
    db: RustHubDatabase
) : Queries(db), ServerCacheDataSource {
    override suspend fun clearServersAndKeys() {
        withContext(Dispatchers.IO) {
            queries.transaction {
                queries.clearServers()
                queries.clearRemoteKeys()
            }
        }
    }
}
