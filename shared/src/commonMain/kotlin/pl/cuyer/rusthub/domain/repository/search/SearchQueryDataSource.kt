package pl.cuyer.rusthub.domain.repository.search

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.SearchQuery

interface SearchQueryDataSource {
    fun getQueries(): Flow<List<SearchQuery>>
    fun upsertQuery(query: SearchQuery)
    fun clearQueries()
    fun deleteByQuery(query: String)
}
