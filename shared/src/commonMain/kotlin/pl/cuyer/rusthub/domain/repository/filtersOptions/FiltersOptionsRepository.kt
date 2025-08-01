package pl.cuyer.rusthub.domain.repository.filtersOptions

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.FiltersOptions

interface FiltersOptionsRepository {
    fun getFiltersOptions(): Flow<Result<FiltersOptions?>>
}