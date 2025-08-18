package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class SaveRaidUseCase(
    private val repository: RaidRepository,
) {
    suspend operator fun invoke(raid: Raid) = repository.upsertRaid(raid)
}
