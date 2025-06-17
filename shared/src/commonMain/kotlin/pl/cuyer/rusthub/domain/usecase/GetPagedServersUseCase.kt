package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import database.ServerEntity
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRemoteMediator
import pl.cuyer.rusthub.domain.repository.server.ServerRepository

class GetPagedServersUseCase(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(query: ServerQuery): Flow<PagingData<ServerEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            remoteMediator = ServerRemoteMediator(
                dataSource,
                api,
                filters
            ),
            pagingSourceFactory = { dataSource.getServersPagingSource() }
        ).flow
    }
}