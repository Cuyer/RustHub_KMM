package pl.cuyer.rusthub.domain.repository.server

interface ServerCacheDataSource {
    /**
     * Clears cached servers and remote keys in a single transaction.
     */
    suspend fun clearServersAndKeys()
}
