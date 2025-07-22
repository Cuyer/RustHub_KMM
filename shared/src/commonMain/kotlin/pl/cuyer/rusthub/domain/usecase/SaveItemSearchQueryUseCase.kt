package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.SearchQuery
import pl.cuyer.rusthub.domain.repository.search.ItemSearchQueryDataSource

class SaveItemSearchQueryUseCase(
    private val dataSource: ItemSearchQueryDataSource
) {
    suspend operator fun invoke(query: SearchQuery) {
        dataSource.upsertQuery(query)
    }
}
