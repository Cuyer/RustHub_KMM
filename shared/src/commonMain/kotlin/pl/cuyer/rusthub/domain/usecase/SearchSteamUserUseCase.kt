package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.domain.repository.steam.SteamRepository

class SearchSteamUserUseCase(
    private val getSteamApiKeyUseCase: GetSteamApiKeyUseCase,
    private val steamRepository: SteamRepository,
) {
    operator fun invoke(query: String): Flow<Result<SteamUser?>> {
        return getSteamApiKeyUseCase().flatMapLatest { keyResult ->
            when (keyResult) {
                is Result.Success -> steamRepository.searchUser(keyResult.data, query)
                is Result.Error -> flowOf(Result.Error(keyResult.exception))
            }
        }
    }
}

