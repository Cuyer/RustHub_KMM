package pl.cuyer.rusthub.data.local.remotekey

import database.RemoteKeyEntity
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toDomain
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.RemoteKey
import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource

class RemoteKeyDataSourceImpl(
    db: RustHubDatabase
) : RemoteKeyDataSource, Queries(db) {

    override fun getKey(id: String): RemoteKey? =
        queries.getRemoteKey(id).executeAsOneOrNull()?.toDomain()

    override fun upsertKey(key: RemoteKey) {
        queries.upsertRemoteKey(
            id = key.id,
            next_page = key.nextPage,
            last_updated = key.lastUpdated
        )
    }

    override fun clearKeys() {
        queries.clearRemoteKeys()
    }
}
