package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.config.ConfigRepository

class GetSteamApiKeyUseCase(
    private val repository: ConfigRepository,
) {
    operator fun invoke(): Flow<Result<String>> = repository.getSteamApiKey()
}

