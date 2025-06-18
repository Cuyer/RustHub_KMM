package pl.cuyer.rusthub.domain.repository.filtersOptions

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.FiltersOptions

interface FiltersOptionsDataSource {
    fun upsertFiltersOptions(filtersOptions: FiltersOptions)
    fun getFiltersOptions(): Flow<FiltersOptions>
    fun clearFiltersOptions()
}