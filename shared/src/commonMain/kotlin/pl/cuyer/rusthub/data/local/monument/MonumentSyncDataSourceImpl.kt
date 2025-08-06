package pl.cuyer.rusthub.data.local.monument

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToOneOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.common.Constants
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.MonumentSyncState
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentSyncDataSource
import pl.cuyer.rusthub.util.CrashReporter

class MonumentSyncDataSourceImpl(db: RustHubDatabase) : MonumentSyncDataSource, Queries(db) {
    override suspend fun setState(state: MonumentSyncState) {
        withContext(Dispatchers.IO) { safeExecute { queries.upsertMonumentSync(id = Constants.DEFAULT_KEY, sync_state = state.name) } }
    }

    override fun observeState(): Flow<MonumentSyncState?> {
        return queries.getMonumentSync(Constants.DEFAULT_KEY)
            .asFlow()
            .mapToOneOrNull(Dispatchers.IO)
            .map { it?.sync_state?.let { value -> MonumentSyncState.valueOf(value) } }
            .catch { e ->
                CrashReporter.recordException(e)
                throw e
            }
    }

    override suspend fun clearState() {
        withContext(Dispatchers.IO) { safeExecute { queries.clearMonumentSync() } }
    }
}
