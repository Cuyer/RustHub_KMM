package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.domain.repository.steam.SteamRepository

/**
 * Searches for Steam users using either a Steam ID or a vanity URL.
 *
 * A Steam ID is the numeric identifier for an account, while a vanity URL is
 * the custom profile address chosen by the user.
 */
class SearchSteamUserUseCase(
    private val getSteamApiKeyUseCase: GetSteamApiKeyUseCase,
    private val steamRepository: SteamRepository,
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    operator fun invoke(queries: List<String>): Flow<Result<List<SteamUser>>> {
        return getSteamApiKeyUseCase().flatMapLatest { keyResult ->
            when (keyResult) {
                is Result.Success -> steamRepository.searchUsers(keyResult.data, queries)
                is Result.Error -> flowOf(Result.Error(keyResult.exception))
            }
        }
    }
}

