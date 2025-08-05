package pl.cuyer.rusthub.domain.repository.server

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import database.ServerEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.ConnectivityException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.CancellationException
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalPagingApi::class, ExperimentalTime::class)
class ServerRemoteMediator(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource,
    private val cacheDataSource: ServerCacheDataSource,
    private val searchQuery: String?
) : RemoteMediator<Int, ServerEntity>() {
    private var nextPage: Int = 0

    override suspend fun initialize(): InitializeAction {
        val current = filters.getFilters().firstOrNull()
        if (current == null) {
            filters.upsertFilters(ServerQuery())
        }
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ServerEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                nextPage = 0
                0
            }
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> nextPage
        }

        return try {
            val query: ServerQuery = filters.getFilters().first() ?: ServerQuery()
            when (val result = api.getServers(
                page,
                state.config.pageSize,
                query,
                searchQuery
            )
                .first()) {
                is Result.Error -> {
                    return if (
                        result.exception is ConnectivityException ||
                        result.exception is ServiceUnavailableException
                    ) {
                        MediatorResult.Success(endOfPaginationReached = true)
                    } else {
                        MediatorResult.Error(result.exception)
                    }
                }
                is Result.Success -> {
                    if (loadType == LoadType.REFRESH) {
                        // Filter or query changed, purge cache before inserting new data
                        cacheDataSource.clearServers()
                    }
                    dataSource.upsertServers(result.data.servers)
                    val end = page >= result.data.totalPages - 1
                    nextPage = if (end) page else page + 1
                    MediatorResult.Success(endOfPaginationReached = end)
                }

            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
            if (e is ConnectivityException || e is ServiceUnavailableException) {
                MediatorResult.Success(endOfPaginationReached = true)
            } else {
                MediatorResult.Error(e)
            }
        }
    }
}

