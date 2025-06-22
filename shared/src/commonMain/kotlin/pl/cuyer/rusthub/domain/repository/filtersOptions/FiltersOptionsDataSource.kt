package pl.cuyer.rusthub.domain.repository.filtersOptions

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.FiltersOptions

interface FiltersOptionsDataSource {
    suspend fun upsertFiltersOptions(filtersOptions: FiltersOptions)
    fun getFiltersOptions(): Flow<FiltersOptions>
    suspend fun clearFiltersOptions()
}