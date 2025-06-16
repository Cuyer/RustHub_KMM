package pl.cuyer.rusthub.data.local.remoteKey

import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.remoteKey.RemoteKeyDataSource

class RemoteKeyDataSourceImpl(
    db: RustHubDatabase
) : RemoteKeyDataSource, Queries(db) {

    override fun findKey(key: String): String? {
        return queries.selectRemoteKey(key).executeAsOneOrNull()?.next_url
    }

    override fun clearRemoteKeys() {
        queries.clearRemoteKeys()
    }

    override fun insertOrReplaceRemoteKey(id: String, nextKey: String?) {
        queries.insertOrReplaceRemoteKey(id, nextKey, null)
    }
}