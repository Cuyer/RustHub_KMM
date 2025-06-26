package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import database.ServerEntity
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRemoteMediator
import pl.cuyer.rusthub.domain.repository.server.ServerRepository

class GetPagedServersUseCase(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource,
    private val remoteKeys: RemoteKeyDataSource,
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        searchQuery: String?,
        favouritesOnly: Boolean,
        subscribedOnly: Boolean
    ): Flow<PagingData<ServerEntity>> {
        return Pager(
            config = PagingConfig(
                pageSize = 40,
                enablePlaceholders = true
            ),
            remoteMediator = ServerRemoteMediator(
                dataSource,
                api,
                filters,
                remoteKeys,
                searchQuery,
                favouritesOnly,
                subscribedOnly
            ),
            pagingSourceFactory = {
                dataSource.getServersPagingSource(
                    searchQuery,
                    favouritesOnly,
                    subscribedOnly
                )
            }
        ).flow
    }
}