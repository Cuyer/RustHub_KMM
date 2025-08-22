package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class GetRaidsUseCase(
    private val repository: RaidRepository,
) {
    operator fun invoke(): Flow<Result<List<Raid>>> = repository.getRaids()
}
