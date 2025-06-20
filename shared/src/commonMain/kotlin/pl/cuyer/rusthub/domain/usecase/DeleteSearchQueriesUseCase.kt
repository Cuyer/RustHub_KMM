package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource

class DeleteSearchQueriesUseCase(
    private val dataSource: SearchQueryDataSource
) {
    operator fun invoke() {
        dataSource.clearQueries()
    }

    operator fun invoke(query: String) {
        dataSource.deleteByQuery(query)
    }
}
