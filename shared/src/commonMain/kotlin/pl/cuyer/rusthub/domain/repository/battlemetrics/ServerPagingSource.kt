package pl.cuyer.rusthub.domain.repository.battlemetrics

import app.cash.paging.PagingSource
import app.cash.paging.PagingState
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.ServerDataSource

class ServerPagingSource(
    private val dataSource: ServerDataSource,
    private val query: ServerQuery
) : PagingSource<Int, ServerInfo>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ServerInfo> {
        val offset = params.key ?: 0
        return try {
            val servers = dataSource.getPagedServers(query, params.loadSize.toLong(), offset.toLong())

            LoadResult.Page(
                data = servers,
                prevKey = if (offset == 0) null else offset - params.loadSize,
                nextKey = if (servers.size < params.loadSize) null else offset + params.loadSize
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, ServerInfo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(state.config.pageSize)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(state.config.pageSize)
        }
    }
}