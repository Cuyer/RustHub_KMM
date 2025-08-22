package pl.cuyer.rusthub.domain.repository.raid

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.Raid

interface RaidRepository {
    fun getRaids(): Flow<Result<List<Raid>>>
    fun createRaid(raid: Raid): Flow<Result<Unit>>
    fun updateRaid(raid: Raid): Flow<Result<Unit>>
    fun deleteRaid(id: String): Flow<Result<Unit>>
}
