package pl.cuyer.rusthub.domain.repository

import database.FiltersEntity
import pl.cuyer.rusthub.domain.model.ServerQuery

interface FiltersDataSource {
    fun getFilters(): FiltersEntity?
    fun upsertFilters(filters: ServerQuery)
    fun clearFilters()
}