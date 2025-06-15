package pl.cuyer.rusthub.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsRemoteMediator

class GetPagedServersUseCase(
    private val api: BattlemetricsClient,
    private val dataSource: ServerDataSource,
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(query: ServerQuery): Flow<PagingData<ServerInfo>> {
        return Pager(
            config = PagingConfig(pageSize = 100, enablePlaceholders = false),
            remoteMediator = BattlemetricsRemoteMediator(api, dataSource),
            pagingSourceFactory = { dataSource.getServersPagingSource(query) }
        ).flow
    }
}