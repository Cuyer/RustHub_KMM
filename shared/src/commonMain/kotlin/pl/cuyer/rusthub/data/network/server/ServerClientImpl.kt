package pl.cuyer.rusthub.data.network.server

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.server.mapper.toDomain
import pl.cuyer.rusthub.data.network.server.model.PagedServerInfoDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.data.network.util.appendNonNull
import pl.cuyer.rusthub.domain.model.PagedServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.server.ServerRepository

class ServerClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : ServerRepository,
    BaseApiResponse(json) {
    override fun getServers(
        page: Int,
        size: Int,
        query: ServerQuery,
        searchQuery: String?
    ): Flow<Result<PagedServerInfo>> {
        return safeApiCall<PagedServerInfoDto> {
            httpClient.get(NetworkConstants.BASE_URL + "servers") {
                url {
                    appendNonNull("page" to page)
                    appendNonNull("size" to size)
                    appendNonNull("wipe" to query.wipe)
                    appendNonNull("ranking" to query.ranking)
                    appendNonNull("playerCount" to query.playerCount)
                    appendNonNull("map" to query.map)
                    appendNonNull("flag" to query.flag)
                    appendNonNull("region" to query.region)
                    appendNonNull("groupLimit" to query.groupLimit)
                    appendNonNull("difficulty" to query.difficulty)
                    appendNonNull("wipeSchedule" to query.wipeSchedule)
                    appendNonNull("order" to query.order)
                    if (!searchQuery.isNullOrBlank()) parameters.append("name", searchQuery)
                    if (query.official == true) parameters.append("official", true.toString())
                    if (query.modded == true) parameters.append("modded", true.toString())
                    appendNonNull("filter" to query.filter)
                }
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
            }
        }
    }
}