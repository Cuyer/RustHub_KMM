package pl.cuyer.rusthub.domain.repository.remoteKey

interface RemoteKeyDataSource {
    fun findNextKey(key: String): String?
    fun findPreviousKey(key: String): String?
    fun clearRemoteKeys()
    fun insertOrReplaceRemoteKey(id: String, nextKey: String?, prevKey: String?)
}