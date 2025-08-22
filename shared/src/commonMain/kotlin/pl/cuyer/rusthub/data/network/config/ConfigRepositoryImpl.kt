package pl.cuyer.rusthub.data.network.config

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.config.ConfigRepository

@Serializable
private data class GoogleClientIdDto(val googleClientId: String)
@Serializable
private data class SteamApiKeyDto(val steamApiKey: String)

class ConfigRepositoryImpl(
    private val httpClient: HttpClient,
    json: Json,
) : ConfigRepository, BaseApiResponse(json) {
    override fun getGoogleClientId(): Flow<Result<String>> {
        return safeApiCall<GoogleClientIdDto> {
            httpClient.get(NetworkConstants.BASE_URL + "google-client-id")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.googleClientId)
                is Result.Error -> result
            }
        }
    }

    override fun getSteamApiKey(): Flow<Result<String>> {
        return safeApiCall<SteamApiKeyDto> {
            httpClient.get(NetworkConstants.BASE_URL + "steam-api-key")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.steamApiKey)
                is Result.Error -> result
            }
        }
    }
}
