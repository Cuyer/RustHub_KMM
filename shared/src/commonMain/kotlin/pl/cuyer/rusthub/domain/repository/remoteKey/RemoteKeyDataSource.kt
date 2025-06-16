package pl.cuyer.rusthub.domain.repository.remoteKey

interface RemoteKeyDataSource {
    fun findKey(key: String): String?
    fun clearRemoteKeys()
    fun insertOrReplaceRemoteKey(id: String, nextKey: String?)
}