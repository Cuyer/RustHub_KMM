package pl.cuyer.rusthub.domain.repository.battlemetrics

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import io.ktor.http.Url
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.io.IOException
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.remoteKey.RemoteKeyDataSource

@OptIn(ExperimentalPagingApi::class)
class BattlemetricsRemoteMediator(
    private val api: BattlemetricsClient,
    private val dataSource: ServerDataSource,
    private val remoteKeyDataSource: RemoteKeyDataSource
) : RemoteMediator<Int, ServerInfo>() {

    companion object {
     private const val REMOTE_KEY_ID = "servers"
    }
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ServerInfo>
    ): MediatorResult {
        val pageKey: String? = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                remoteKeyDataSource.findKey(REMOTE_KEY_ID)
            }
        }

        // If appending but no next_key, we've reached the end
        if (loadType == LoadType.APPEND && pageKey == null) {
            return MediatorResult.Success(endOfPaginationReached = true)
        }

        return try {
            // Call your client and skip the initial Loading emission
            val result = api
                .getServers(
                    size = state.config.pageSize,
                    sort = "rank",
                    key = pageKey
                )
                .filter { it !is Result.Loading }
                .first()

            val page = when (result) {
                is Result.Success -> result.data
                is Result.Error -> throw result.exception
                else -> throw IllegalStateException("Unexpected result: $result")
            }

            val entities = page.data.map { it.toServerInfo() }
            val nextKey = extractNextPageKey(page.links?.next)

            // Persist within one transaction
            if (loadType == LoadType.REFRESH) {
                dataSource.clearNotFavouriteServers()
                remoteKeyDataSource.clearRemoteKeys()
            }
            dataSource.upsertServers(entities)

            remoteKeyDataSource.insertOrReplaceRemoteKey(
                id = REMOTE_KEY_ID,
                nextKey = nextKey
            )

            MediatorResult.Success(endOfPaginationReached = (nextKey == null))
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }
}

fun extractNextPageKey(url: String?): String? {
    if (url == null) return null
    val parsed = Url(url)
    return parsed.parameters["page[key]"]
}