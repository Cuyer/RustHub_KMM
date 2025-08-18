package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class ObserveRaidsUseCase(
    private val repository: RaidRepository,
) {
    operator fun invoke(): Flow<List<Raid>> = repository.observeRaids()
}
