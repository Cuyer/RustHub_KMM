package pl.cuyer.rusthub.data.network.battlemetrics

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import app.cash.paging.RemoteMediator
import database.Server
import kotlinx.io.IOException
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient

@OptIn(ExperimentalPagingApi::class)
class BattlemetricsRemoteMediator(
    private val api: BattlemetricsClient,
    private val db: RustHubDatabase,
    private val sort: String
) : RemoteMediator<Int, Server>() {
    private val REMOTE_KEY_ID = "servers"
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Server>
    ): MediatorResult {
        // Determine the page-key to request
        val pageKey: String? = when (loadType) {
            LoadType.REFRESH -> null
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                db.remoteKeysQueries
                    .selectRemoteKey(REMOTE_KEY_ID)
                    .executeAsOneOrNull()
                    ?.next_key
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
                    sort = sort,
                    key = pageKey
                )
                .filter { it !is Result.Loading }
                .first()

            val page = when (result) {
                is Result.Success -> result.data
                is Result.Error   -> throw result.exception
                else               -> throw IllegalStateException("Unexpected result: $result")
            }

            val entities = page.data.map { mapper.toEntity(it) }
            val nextKey  = extractNextPageKey(page.links?.next)

            // Persist within one transaction
            db.transaction {
                if (loadType == LoadType.REFRESH) {
                    db.serverQueries.clearAllServers()
                    db.remoteKeysQueries.clearRemoteKeys()
                }
                entities.forEach { db.serverQueries.insertServer(it) }
                db.remoteKeysQueries.insertOrReplaceRemoteKey(
                    id       = REMOTE_KEY_ID,
                    next_key = nextKey
                )
            }

            MediatorResult.Success(endOfPaginationReached = (nextKey == null))
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}