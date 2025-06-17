package pl.cuyer.rusthub.data.network.server

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.data.network.util.appendNonNull
import pl.cuyer.rusthub.domain.model.ServerInfo
import pl.cuyer.rusthub.domain.model.ServerQuery
import pl.cuyer.rusthub.domain.repository.server.ServerRepository

class ServerClientImpl(private val httpClient: HttpClient) : ServerRepository,
    BaseApiResponse() {
    override fun getServers(page: Int, size: Int, query: ServerQuery): Flow<Result<ServerInfo>> {
        return safeApiCall {
            httpClient.get(NetworkConstants.BASE_URL) {
                url {
                    appendNonNull("page" to page)
                    appendNonNull("size" to size)
                    appendNonNull("wipe" to query.wipe)
                    appendNonNull("ranking" to query.ranking)
                    appendNonNull("modded" to query.modded)
                    appendNonNull("playerCount" to query.playerCount)
                    appendNonNull("map" to query.map)
                    appendNonNull("flag" to query.flag)
                    appendNonNull("region" to query.region)
                    appendNonNull("groupLimit" to query.groupLimit)
                    appendNonNull("difficulty" to query.difficulty)
                    appendNonNull("wipeSchedule" to query.wipeSchedule)
                    appendNonNull("official" to query.official)
                    appendNonNull("order" to query.order)
                }
            }
        }
    }
}