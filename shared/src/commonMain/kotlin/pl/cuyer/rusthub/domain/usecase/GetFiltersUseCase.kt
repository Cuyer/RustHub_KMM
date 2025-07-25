package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource

class GetFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    operator fun invoke(): Flow<ServerQuery?> {
        return dataSource.getFilters()
    }
}