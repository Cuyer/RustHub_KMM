package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingData
import database.Server
import io.ktor.http.Url
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.Clock
import kotlinx.datetime.Clock.System
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Constants.DEFAULT_KEY
import pl.cuyer.rusthub.common.Constants.DEFAULT_PAGES_FETCHED
import pl.cuyer.rusthub.common.Constants.DEFAULT_REFRESH_TIMEOUT
import pl.cuyer.rusthub.common.Constants.DEFAULT_SORT
import pl.cuyer.rusthub.common.Constants.RETRY_THRESHOLD
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.battlemetrics.model.error.BattlemetricsError
import pl.cuyer.rusthub.domain.mapper.toServerInfo
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.ServerDataSource
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient
import pl.cuyer.rusthub.domain.repository.remoteKey.RemoteKeyDataSource
import pl.cuyer.rusthub.domain.repository.timestamp.TimestampDataSource

class FetchAllServersUseCase(
    private val dataSource: ServerDataSource,
    private val client: BattlemetricsClient,
    private val remoteKeyDataSource: RemoteKeyDataSource,
    private val timestampDataSource: TimestampDataSource,
    private val json: Json
) {
    @OptIn(ExperimentalPagingApi::class)
    operator fun invoke(pageSize: Int = 100): Flow<Result<Unit>> = flow {
        emit(Result.Loading)
        var nextKey: String? = remoteKeyDataSource.findNextKey(DEFAULT_KEY)
        var errorCount = 0
        var page = 0
        val lastFetch: Instant = timestampDataSource.getOrInitTimestamp()
        val now: Instant = System.now()
        val minutesSinceLastFetch = now.minus(lastFetch).inWholeMinutes

        if (minutesSinceLastFetch < DEFAULT_REFRESH_TIMEOUT) {
            emit(Result.Error(Exception("Last refreshed $minutesSinceLastFetch minutes ago, skipping.")))
            return@flow
        }
        do {
            val result = client.getServers(pageSize, DEFAULT_SORT, nextKey)
                .first() { it !is Result.Loading }
            when (result) {
                is Result.Success -> {
                    errorCount = 0
                    ++page
                    val page = result.data
                    dataSource.upsertServers(page.data.map { it.toServerInfo() })
                    nextKey = extractPageKey(page.links?.next)
                    remoteKeyDataSource.insertOrReplaceRemoteKey(
                        id = DEFAULT_KEY,
                        nextKey = nextKey,
                        prevKey = null
                    )
                }

                is Result.Error -> {
                    val errorJson = result.exception.message.orEmpty()
                    val parsedError = runCatching {
                        json.decodeFromString<BattlemetricsError>(errorJson)
                    }.getOrNull()

                    val tryAgainInstant = parsedError
                        ?.errors
                        ?.firstOrNull()
                        ?.meta
                        ?.tryAgain
                        ?.let { runCatching { Instant.parse(it) }.getOrNull() }

                    tryAgainInstant?.let { retryTime ->
                        val delayDuration = retryTime.toEpochMilliseconds() - System.now().toEpochMilliseconds()
                        if (delayDuration > 0) delay(delayDuration.plus(1000L))
                    }

                    if (++errorCount == RETRY_THRESHOLD) {
                        remoteKeyDataSource.clearRemoteKeys()
                        emit(Result.Error(result.exception))
                        return@flow
                    }
                }

                else -> Unit
            }
        } while (nextKey != null && page < DEFAULT_PAGES_FETCHED)
        timestampDataSource.insertTimestamp(System.now())
        emit(Result.Success(Unit))
    }

    fun extractPageKey(url: String?): String? {
        if (url == null) return null
        val parsed = Url(url)
        return parsed.parameters["page[key]"]
    }
}