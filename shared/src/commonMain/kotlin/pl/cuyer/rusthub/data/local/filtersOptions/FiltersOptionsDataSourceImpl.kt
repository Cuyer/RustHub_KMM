package pl.cuyer.rusthub.data.local.filtersOptions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
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

    override fun getFiltersOptions(): Flow<FiltersOptions> = flow {
        val rawOptions = safeQuery(null) { queries.getFiltersOptions(DEFAULT_KEY).executeAsOneOrNull() }
        emit(rawOptions.toDomain())
    }.catch { e ->
        CrashReporter.recordException(e)
        throw e
    }.flowOn(Dispatchers.IO)

    override suspend fun clearFiltersOptions() {
        withContext(Dispatchers.IO) {
            safeExecute { queries.clearFiltersOptions() }
        }
    }
}