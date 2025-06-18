package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository

class GetFiltersOptions(
    private val api: FiltersOptionsRepository,
    private val dataSource: FiltersOptionsDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(): Flow<FiltersOptions?> {
        return dataSource.getFiltersOptions()
    }
}