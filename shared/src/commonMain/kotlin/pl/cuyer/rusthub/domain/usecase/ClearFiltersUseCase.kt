package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource

class ClearFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    suspend operator fun invoke() {
        return dataSource.clearFilters()
    }
}