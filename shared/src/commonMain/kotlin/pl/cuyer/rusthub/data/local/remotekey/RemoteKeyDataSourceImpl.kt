package pl.cuyer.rusthub.data.local.remotekey

import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toDomain
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.RemoteKey
import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource

class RemoteKeyDataSourceImpl(
    db: RustHubDatabase
) : RemoteKeyDataSource, Queries(db) {

    override suspend fun getKey(id: String): RemoteKey? =
        withContext(Dispatchers.IO) {
            queries.getRemoteKey(id).executeAsOneOrNull()?.toDomain()
        }

    override suspend fun upsertKey(key: RemoteKey) {
        withContext(Dispatchers.IO) {
            queries.upsertRemoteKey(
                id = key.id,
                next_page = key.nextPage,
                last_updated = key.lastUpdated
            )
        }
    }

    override suspend fun clearKeys() {
        withContext(Dispatchers.IO) {
            queries.clearRemoteKeys()
        }
    }
}
