package pl.cuyer.rusthub.domain.repository

import pl.cuyer.rusthub.domain.model.RemoteKey

interface RemoteKeyDataSource {
    fun getKey(id: String): RemoteKey?
    suspend fun upsertKey(key: RemoteKey)
    suspend fun clearKeys()
}
