package pl.cuyer.rusthub.data.raid

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository
import pl.cuyer.rusthub.domain.repository.raid.local.RaidDataSource

class RaidRepositoryImpl(
    private val local: RaidDataSource
) : RaidRepository {
    override fun observeRaids(): Flow<List<Raid>> = local.observeRaids()

    override suspend fun upsertRaid(raid: Raid) {
        local.upsertRaid(raid)
    }

    override suspend fun deleteRaids(ids: List<String>) {
        ids.forEach { local.deleteRaid(it) }
    }
}
