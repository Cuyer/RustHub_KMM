package pl.cuyer.rusthub.domain.usecase

import app.cash.paging.ExperimentalPagingApi
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import kotlin.time.ExperimentalTime

class ClearFiltersUseCase(
    private val dataSource: FiltersDataSource
) {
    @OptIn(ExperimentalPagingApi::class, ExperimentalTime::class)
    suspend operator fun invoke() {
        dataSource.upsertFilters(ServerQuery())
    }}