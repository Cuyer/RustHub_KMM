package pl.cuyer.rusthub.domain.repository.search

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.SearchQuery

interface MonumentSearchQueryDataSource {
    fun getQueries(): Flow<List<SearchQuery>>
    suspend fun upsertQuery(query: SearchQuery)
    suspend fun clearQueries()
    suspend fun deleteByQuery(query: String)
}

