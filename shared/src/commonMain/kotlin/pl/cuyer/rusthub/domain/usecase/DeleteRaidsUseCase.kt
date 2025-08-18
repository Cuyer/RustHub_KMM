package pl.cuyer.rusthub.domain.usecase

import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class DeleteRaidsUseCase(
    private val repository: RaidRepository,
) {
    suspend operator fun invoke(ids: List<String>) = repository.deleteRaids(ids)
}
