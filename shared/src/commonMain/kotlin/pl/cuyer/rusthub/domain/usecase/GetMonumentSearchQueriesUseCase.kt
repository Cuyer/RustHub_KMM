package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.MonumentSearchQueryDataSource

class GetMonumentSearchQueriesUseCase(
    private val dataSource: MonumentSearchQueryDataSource
) {
    operator fun invoke(): Flow<List<SearchQuery>> {
        return dataSource.getQueries()
    }
}

