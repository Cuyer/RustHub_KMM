package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.battlemetrics.model.BattlemetricsPage
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient

class GetServersUseCase(
    private val repository: BattlemetricsClient
) {
    operator fun invoke(
        pageSize: Int = 20,
        sort: String = "rank",
        key: String?
    ): Flow<Result<BattlemetricsPage>> =
        repository.getServers(pageSize, sort, key)
}