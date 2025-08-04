package pl.cuyer.rusthub.domain.repository.server

interface ServerCacheDataSource {
    /**
     * Clears cached servers.
     */
    suspend fun clearServers()
}
