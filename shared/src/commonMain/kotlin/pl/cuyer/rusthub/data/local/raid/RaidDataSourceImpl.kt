package pl.cuyer.rusthub.data.local.raid

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.local.RaidDataSource
import pl.cuyer.rusthub.data.local.mapper.toRaid
import pl.cuyer.rusthub.util.CrashReporter

class RaidDataSourceImpl(
    db: RustHubDatabase,
) : RaidDataSource, Queries(db) {

    override fun observeRaids(): Flow<List<Raid>> {
        return queries.getRaids()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { list -> list.map { it.toRaid() } }
            .catch { e ->
                CrashReporter.recordException(e)
                throw e
            }
    }

    override suspend fun upsertRaid(raid: Raid) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.upsertRaid(
                    id = raid.id,
                    name = raid.name,
                    dateTime = raid.dateTime.toString(),
                    steamIds = raid.steamIds.joinToString(","),
                    description = raid.description
                )
            }
        }
    }

    override suspend fun deleteRaid(id: String) {
        withContext(Dispatchers.IO) {
            safeExecute {
                queries.deleteRaid(id = id)
            }
        }
    }
}
