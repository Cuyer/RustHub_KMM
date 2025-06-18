package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import pl.cuyer.rusthub.domain.repository.FiltersDataSource

class ClearFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke() {
        return dataSource.clearFilters()
    }
}