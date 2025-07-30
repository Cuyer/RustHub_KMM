package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource

class SaveFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    suspend operator fun invoke(filters: ServerQuery) {
        return dataSource.upsertFilters(filters)
    }
}