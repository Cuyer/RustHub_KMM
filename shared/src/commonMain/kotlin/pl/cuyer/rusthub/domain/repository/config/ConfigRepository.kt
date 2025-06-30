package pl.cuyer.rusthub.domain.repository.config

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result

interface ConfigRepository {
    fun getGoogleClientId(): Flow<Result<String>>
}
