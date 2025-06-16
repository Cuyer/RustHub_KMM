package pl.cuyer.rusthub.domain.usecase

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsRemoteMediator
import pl.cuyer.rusthub.domain.repository.remoteKey.RemoteKeyDataSource

class GetPagedServersUseCase(
    private val api: BattlemetricsClient,
    private val dataSource: ServerDataSource,
    private val remoteKeyDataSource: RemoteKeyDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(query: ServerQuery): Flow<PagingData<ServerInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = false
            ),
            remoteMediator = BattlemetricsRemoteMediator(api, dataSource, remoteKeyDataSource),
            pagingSourceFactory = { dataSource.getServersPagingSource(query) }
        ).flow
    }
}