package pl.cuyer.rusthub.domain.repository.raid.local

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Raid

interface RaidDataSource {
    fun observeRaids(): Flow<List<Raid>>
    suspend fun upsertRaid(raid: Raid)
    suspend fun deleteRaid(id: String)
}
