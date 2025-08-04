package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import androidx.paging.map
import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import pl.cuyer.rusthub.data.local.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerRemoteMediator
import pl.cuyer.rusthub.domain.repository.server.ServerRepository
import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource

class GetPagedServersUseCase(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource,
    private val cacheDataSource: ServerCacheDataSource,
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(
        searchQuery: String?
    ): Flow<PagingData<ServerInfo>> {
        return Pager(
            config = PagingConfig(
                pageSize = 10,
                enablePlaceholders = true
            ),
            remoteMediator = ServerRemoteMediator(
                dataSource,
                api,
                filters,
                cacheDataSource,
                searchQuery
            ),
            pagingSourceFactory = {
                dataSource.getServersPagingSource(
                    searchQuery
                )
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toServerInfo() }
        }
    }
}