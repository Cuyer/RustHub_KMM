package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository

class GetFiltersOptionsUseCase(
    private val api: FiltersOptionsRepository,
    private val dataSource: FiltersOptionsDataSource
) {
    operator fun invoke(): Flow<FiltersOptions?> = channelFlow {
        val localJob = launch {
            dataSource.getFiltersOptions().collect { send(it) }
        }
        val remoteJob = launch {
            api.getFiltersOptions().collect { result ->
                if (result is Result.Success && result.data != null) {
                    dataSource.upsertFiltersOptions(result.data)
                }
            }
        }
        awaitClose {
            localJob.cancel()
            remoteJob.cancel()
        }
    }.distinctUntilChanged()
}
