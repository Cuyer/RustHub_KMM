package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class CreateRaidUseCase(
    private val repository: RaidRepository,
) {
    operator fun invoke(raid: Raid): Flow<Result<Unit>> = repository.createRaid(raid)
}

