package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.MonumentSearchQueryDataSource

class SaveMonumentSearchQueryUseCase(
    private val dataSource: MonumentSearchQueryDataSource
) {
    suspend operator fun invoke(query: SearchQuery) {
        dataSource.upsertQuery(query)
    }
}

