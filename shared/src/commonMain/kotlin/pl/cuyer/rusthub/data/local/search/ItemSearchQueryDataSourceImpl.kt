package pl.cuyer.rusthub.data.local.search

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.data.local.mapper.toDomain
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.ItemSearchQueryDataSource

class ItemSearchQueryDataSourceImpl(
    db: RustHubDatabase
) : ItemSearchQueryDataSource, Queries(db) {

    override fun getQueries(): Flow<List<SearchQuery>> {
        return queries.getItemSearchQueries()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list ->
                list.map { it.toDomain() }
            }
    }

    override suspend fun upsertQuery(query: SearchQuery) {
        withContext(Dispatchers.IO) {
            queries.upsertItemSearchQuery(
                query = query.query,
                timestamp = query.timestamp.toString()
            )
        }
    }

    override suspend fun clearQueries() {
        withContext(Dispatchers.IO) { queries.clearItemSearchQueries() }
    }

    override suspend fun deleteByQuery(query: String) {
        withContext(Dispatchers.IO) { queries.deleteItemSearchQueryByQuery(query) }
    }
}
