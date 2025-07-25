package pl.cuyer.rusthub.domain.repository

import pl.cuyer.rusthub.domain.model.RemoteKey

interface RemoteKeyDataSource {
    suspend fun getKey(id: String): RemoteKey?
    suspend fun upsertKey(key: RemoteKey)
    suspend fun clearKeys()
}
