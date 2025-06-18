package pl.cuyer.rusthub.data.local.filtersOptions

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource

class FiltersOptionsDataSourceImpl(
    db: RustHubDatabase
) : Queries(db), FiltersOptionsDataSource {

    override fun upsertFiltersOptions(filtersOptions: FiltersOptions) {
        TODO("Not yet implemented")
    }

    override fun getFiltersOptions(): Flow<FiltersOptions?> {
        TODO("Not yet implemented")
    }

    override fun clearFiltersOptions() {
        TODO("Not yet implemented")
    }
}