package pl.cuyer.rusthub.domain.repository.steam

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.SteamUser

interface SteamRepository {
    fun searchUsers(apiKey: String, queries: List<String>): Flow<Result<List<SteamUser>>>
}

