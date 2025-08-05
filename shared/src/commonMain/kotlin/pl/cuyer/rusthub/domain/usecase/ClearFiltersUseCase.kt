package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class ClearFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    suspend operator fun invoke() {
        dataSource.upsertFilters(ServerQuery())
    }
}