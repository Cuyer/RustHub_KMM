package pl.cuyer.rusthub.domain.repository.server

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import database.ServerEntity
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.exception.ConnectivityException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.model.RemoteKey
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.RemoteKeyDataSource
import pl.cuyer.rusthub.domain.repository.filters.FiltersDataSource
import pl.cuyer.rusthub.domain.repository.server.ServerCacheDataSource
import kotlinx.datetime.Clock
import pl.cuyer.rusthub.util.CrashReporter
import kotlinx.coroutines.CancellationException

@OptIn(ExperimentalPagingApi::class)
class ServerRemoteMediator(
    private val dataSource: ServerDataSource,
    private val api: ServerRepository,
    private val filters: FiltersDataSource,
    private val remoteKeys: RemoteKeyDataSource,
    private val cacheDataSource: ServerCacheDataSource,
    private val searchQuery: String?
) : RemoteMediator<Int, ServerEntity>() {
    private val keyId = DEFAULT_KEY
    private val cacheTimeoutMillis = 60_000L

    override suspend fun initialize(): InitializeAction {
        val current = filters.getFilters().firstOrNull()
        if (current == null) {
            filters.upsertFilters(ServerQuery())
        }
        val key = remoteKeys.getKey(keyId)
        val hasServers = dataSource.hasServers()
        if ((key == null && hasServers) || (key != null && !hasServers)) {
            // Detected cache corruption or upgrade. Clear all to recover.
            cacheDataSource.clearServersAndKeys()
            return InitializeAction.LAUNCH_INITIAL_REFRESH
        }
        val now = Clock.System.now().toEpochMilliseconds()
        return if (key == null || now - key.lastUpdated > cacheTimeoutMillis) {
            cacheDataSource.clearServersAndKeys()
            InitializeAction.LAUNCH_INITIAL_REFRESH
        } else {
            InitializeAction.SKIP_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ServerEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 0
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val key = remoteKeys.getKey(keyId) ?: return MediatorResult.Success(true)
                key.nextPage?.toInt() ?: return MediatorResult.Success(true)
            }
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
                        // Filter or query changed, purge cache and keys before inserting new data
                        cacheDataSource.clearServersAndKeys()
                    }
                    Napier.d(
                        "Fetched servers size: ${result.data.servers.size}",
                        tag = "ServerRemoteMediator"
                    )
                    dataSource.upsertServers(result.data.servers)
                    val end = page >= result.data.totalPages - 1
                    val nextPage = if (end) null else page + 1
                    val now = Clock.System.now().toEpochMilliseconds()
                    remoteKeys.upsertKey(
                        RemoteKey(
                            id = keyId,
                            nextPage = nextPage?.toLong(),
                            lastUpdated = now
                        )
                    )
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
    }}
