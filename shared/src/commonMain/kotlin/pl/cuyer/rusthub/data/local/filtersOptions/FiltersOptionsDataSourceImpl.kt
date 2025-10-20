package pl.cuyer.rusthub.data.local.filtersOptions

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsDataSource
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.local.mapper.toDomain

class FiltersOptionsDataSourceImpl(
    db: RustHubDatabase
) : Queries(db), FiltersOptionsDataSource {

    override suspend fun upsertFiltersOptions(filtersOptions: FiltersOptions) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.upsertFiltersOptions(
                    id = DEFAULT_KEY,
                    options = Json.encodeToString(filtersOptions)
                )
            }
        }
    }

    override fun getFiltersOptions(): Flow<FiltersOptions> {
        return queries
            .getFiltersOptions(DEFAULT_KEY)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map {  it.toDomain() }
            .flowOn(Dispatchers.Default)
            .catch { e ->
                CrashReporter.recordException(e)
                throw e
            }
    }

    override suspend fun clearFiltersOptions() {
        withContext(Dispatchers.IO) {
            safeExecute { queries.clearFiltersOptions() }
        }
    }
}
