package pl.cuyer.rusthub.domain.repository.server

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import database.ServerEntity
import kotlinx.coroutines.flow.first
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.mapper.toServerQuery
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.ServerDataSource

@OptIn(ExperimentalPagingApi::class)
class ServerRemoteMediator(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource
) : RemoteMediator<Int, ServerEntity>() {
    private var currentPage: Int = 0
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ServerEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 0.also { currentPage = 0 }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> currentPage + 1
        }

        return try {
            val query: ServerQuery = filters.getFilters()?.toServerQuery() ?: ServerQuery()
            when (val result = api.getServers(page, state.config.pageSize, query)
                .first { it !is Result.Loading }) {
                is Result.Error -> MediatorResult.Error(result.exception)
                is Result.Success -> {
                    if (loadType == LoadType.REFRESH) {
                        dataSource.deleteServers()
                    }
                    dataSource.upsertServers(result.data.servers)
                    currentPage = page
                    val end = page >= result.data.totalPages - 1
                    MediatorResult.Success(endOfPaginationReached = end)
                }

                Result.Loading -> MediatorResult.Success(endOfPaginationReached = false)
            }
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}