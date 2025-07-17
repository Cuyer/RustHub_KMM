package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.HttpStatusException
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository

class GetFiltersOptionsUseCase(
    private val api: FiltersOptionsRepository,
    private val dataSource: FiltersOptionsDataSource
) {
    operator fun invoke(): Flow<FiltersOptions?> = flow {

        emitAll(dataSource.getFiltersOptions())

        api.getFiltersOptions().collectLatest { result ->
            if (result is Result.Success && result.data != null) {
                dataSource.upsertFiltersOptions(result.data)
            }
        }
    }.distinctUntilChanged()
}