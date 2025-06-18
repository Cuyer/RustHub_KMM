package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.FiltersDataSource

class SaveFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(filters: ServerQuery) {
        return dataSource.upsertFilters(filters)
    }
}