package pl.cuyer.rusthub.data.local.search

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toDomain
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource

class SearchQueryDataSourceImpl(
    db: RustHubDatabase
) : SearchQueryDataSource, Queries(db) {

    override fun getQueries(): Flow<List<SearchQuery>> {
        return queries.getSearchQueries()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                Napier.d("getQueries: $list")
                list.map {
                    it.toDomain()
                }
            }
    }

    override fun upsertQuery(query: SearchQuery) {
        queries.upsertSearchQuery(
            query = query.query,
            timestamp = query.timestamp.toString()
        )
    }

    override fun clearQueries() {
        queries.clearSearchQueries()
    }

    override fun deleteByQuery(query: String) {
        queries.deleteSearchQueryByQuery(query)
    }
}
