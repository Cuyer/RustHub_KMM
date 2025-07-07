package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource

class DeleteSearchQueriesUseCase(
    private val dataSource: SearchQueryDataSource
) {
    suspend operator fun invoke() {
        dataSource.clearQueries()
    }

    suspend operator fun invoke(query: String) {
        dataSource.deleteByQuery(query)
    }
}
