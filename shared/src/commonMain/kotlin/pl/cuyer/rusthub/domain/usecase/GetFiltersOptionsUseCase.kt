package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository

class GetFiltersOptionsUseCase(
    private val api: FiltersOptionsRepository,
    private val dataSource: FiltersOptionsDataSource
) {
    operator fun invoke(): Flow<FiltersOptions?> = flow {
        api.getFiltersOptions().collectLatest {
            when (it) {
                is Result.Success -> it.data?.let { data -> dataSource.upsertFiltersOptions(data) }
                is Result.Error -> Unit
                is Result.Loading -> Unit
            }
        }
        emitAll(dataSource.getFiltersOptions())
    }
}