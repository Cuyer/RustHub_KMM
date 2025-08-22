package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.local.RaidDataSource

class ObserveRaidsUseCase(
    private val dataSource: RaidDataSource,
) {
    operator fun invoke(): Flow<List<Raid>> = dataSource.observeRaids()
}

