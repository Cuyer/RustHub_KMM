package pl.cuyer.rusthub.domain.repository.monument

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.Monument

interface MonumentRepository {
    fun getMonuments(): Flow<Result<List<Monument>>>
}
