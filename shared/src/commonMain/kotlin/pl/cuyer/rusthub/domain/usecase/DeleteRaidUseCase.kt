package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class DeleteRaidUseCase(
    private val repository: RaidRepository,
) {
    operator fun invoke(id: String): Flow<Result<Unit>> = repository.deleteRaid(id)
}
