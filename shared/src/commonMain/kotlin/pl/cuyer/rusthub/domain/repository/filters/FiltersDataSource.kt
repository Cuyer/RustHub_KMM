package pl.cuyer.rusthub.domain.repository.filters

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerQuery

interface FiltersDataSource {
    fun getFilters(): Flow<ServerQuery?>
    fun upsertFilters(filters: ServerQuery)
    fun clearFilters()
}