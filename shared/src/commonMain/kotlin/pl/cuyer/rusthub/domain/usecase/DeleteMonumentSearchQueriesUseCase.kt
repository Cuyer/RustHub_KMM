package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.search.MonumentSearchQueryDataSource

class DeleteMonumentSearchQueriesUseCase(
    private val dataSource: MonumentSearchQueryDataSource
) {
    suspend operator fun invoke() {
        dataSource.clearQueries()
    }

    suspend operator fun invoke(query: String) {
        dataSource.deleteByQuery(query)
    }
}

