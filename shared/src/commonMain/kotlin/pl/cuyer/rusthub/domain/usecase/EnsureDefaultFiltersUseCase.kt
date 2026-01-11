package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource

class EnsureDefaultFiltersUseCase(
    private val dataSource: FiltersDataSource,
) {
    suspend operator fun invoke() {
        dataSource.ensureDefaultFilters()
    }
}
