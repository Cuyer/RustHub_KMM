package pl.cuyer.rusthub.data.network.raid

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.raid.mapper.toDomain
import pl.cuyer.rusthub.data.network.raid.mapper.toDto
import pl.cuyer.rusthub.data.network.raid.model.RaidDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.Raid
import pl.cuyer.rusthub.domain.repository.raid.RaidRepository

class RaidClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : RaidRepository, BaseApiResponse(json) {
    override fun getRaids(): Flow<Result<List<Raid>>> {
        return safeApiCall<List<RaidDto>> {
            httpClient.get(NetworkConstants.BASE_URL + "raids")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.map { it.toDomain() })
                is Result.Error -> result
            }
        }
    }

    override fun createRaid(raid: Raid): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "raids") {
                setBody(raid.toDto())
            }
        }
    }

    override fun updateRaid(raid: Raid): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.patch(NetworkConstants.BASE_URL + "raids/${raid.id}") {
                setBody(raid.toDto())
            }
        }
    }

    override fun deleteRaid(id: String): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.delete(NetworkConstants.BASE_URL + "raids/$id")
        }
    }
}

