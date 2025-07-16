package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository

class GetFiltersOptionsUseCase(
    private val api: FiltersOptionsRepository,
    private val dataSource: FiltersOptionsDataSource
) {
    operator fun invoke(): Flow<FiltersOptions?> = channelFlow {
        launch {
            api.getFiltersOptions().collectLatest {
                when (it) {
                    is Result.Success -> it.data?.let { data -> dataSource.upsertFiltersOptions(data) }
                    is Result.Error -> when (it.exception) {
                        is NetworkUnavailableException,
                        is ServiceUnavailableException -> Unit
                        else -> throw it.exception
                    }
                }
            }
        }
        launch {
            dataSource.getFiltersOptions().collect { send(it) }
        }
    }}