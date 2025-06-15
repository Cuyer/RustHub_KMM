package pl.cuyer.rusthub.data.network.battlemetrics

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.Url
import io.ktor.http.parameters
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import pl.cuyer.rusthub.data.network.battlemetrics.model.BattlemetricsPage
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.battlemetrics.BattlemetricsClient

class BattlemetricsClientImpl(private val httpClient: HttpClient) : BattlemetricsClient,
    BaseApiResponse() {
    override fun getServers(size: Int, sort: String, key: String?): Flow<Result<BattlemetricsPage>> {
        return safeApiCall {
            httpClient.get(NetworkConstants.BATTLEMETRICS_BASE_URL) {
                url {
                    parameters.append("page[size]", size.toString())
                    parameters.append("sort", sort)
                    parameters.append("filter[game]", "rust")
                    key?.let {
                        parameters.append("page[key]", it)
                    }
                }
            }
        }
    }
}