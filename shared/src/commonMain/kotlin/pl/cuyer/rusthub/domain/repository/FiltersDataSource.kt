package pl.cuyer.rusthub.domain.repository

import pl.cuyer.rusthub.domain.model.ServerQuery

interface FiltersDataSource {
    fun getFilters(): ServerQuery?
    fun upsertFilters(filters: ServerQuery)
    fun clearFilters()
}