package pl.cuyer.rusthub.domain.repository.battlemetrics

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.data.network.battlemetrics.model.BattlemetricsPage
import pl.cuyer.rusthub.common.Result

interface BattlemetricsClient {
    fun getServers(size: Int, sort: String, key: String?): Flow<Result<BattlemetricsPage>>
}