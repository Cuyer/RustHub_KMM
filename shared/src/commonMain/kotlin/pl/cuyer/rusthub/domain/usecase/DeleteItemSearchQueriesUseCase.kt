package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.search.ItemSearchQueryDataSource

class DeleteItemSearchQueriesUseCase(
    private val dataSource: ItemSearchQueryDataSource
) {
    suspend operator fun invoke() {
        dataSource.clearQueries()
    }

    suspend operator fun invoke(query: String) {
        dataSource.deleteByQuery(query)
    }
}
