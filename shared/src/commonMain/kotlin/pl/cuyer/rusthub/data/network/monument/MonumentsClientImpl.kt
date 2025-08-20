package pl.cuyer.rusthub.data.network.monument

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.monument.mapper.toDomain
import pl.cuyer.rusthub.data.network.monument.model.MonumentsResponseDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.repository.monument.MonumentRepository

class MonumentsClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : MonumentRepository, BaseApiResponse(json) {
    override fun getMonuments(): Flow<Result<List<Monument>>> {
        return safeApiCall<MonumentsResponseDto> {
            httpClient.get(NetworkConstants.BASE_URL + "monuments")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.monuments.map { it.toDomain() })
                is Result.Error -> result
            }
        }
    }
}
