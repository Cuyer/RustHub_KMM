package pl.cuyer.rusthub.data.network.rustmap

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.url
import kotlinx.coroutines.flow.Flow
import pl.cuyer.domain.models.server.rustmap.MapResponse
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.rustmap.RustmapsClient

class RustmapsClientImpl(
    private val httpClient: HttpClient
): RustmapsClient, BaseApiResponse() {
    override fun fetchMap(mapId: String): Flow<Result<MapResponse>> {
        return safeApiCall {
            httpClient.get {
                url(NetworkConstants.RUSTMAPS_BASE_URL + mapId)
            }
        }
    }
}