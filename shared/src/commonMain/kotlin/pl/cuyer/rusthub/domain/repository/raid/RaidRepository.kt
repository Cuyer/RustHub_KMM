package pl.cuyer.rusthub.domain.repository.raid

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Raid

interface RaidRepository {
    fun observeRaids(): Flow<List<Raid>>
    suspend fun upsertRaid(raid: Raid)
    suspend fun deleteRaids(ids: List<String>)
}
