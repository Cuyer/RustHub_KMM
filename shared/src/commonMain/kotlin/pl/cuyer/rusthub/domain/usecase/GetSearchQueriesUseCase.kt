package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource

class GetSearchQueriesUseCase(
    private val dataSource: SearchQueryDataSource
) {
    operator fun invoke(): Flow<List<SearchQuery>> {
        return dataSource.getQueries()
    }
}
