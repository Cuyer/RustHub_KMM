package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.SearchQueryDataSource

class SaveSearchQueryUseCase(
    private val dataSource: SearchQueryDataSource
) {
    suspend operator fun invoke(query: SearchQuery) {
        dataSource.upsertQuery(query)
    }
}
