package pl.cuyer.rusthub.data.network.steam

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.domain.model.SteamUser
import pl.cuyer.rusthub.domain.repository.steam.SteamRepository

@Serializable
private data class ResolveVanityResponseDto(val response: ResolveVanityDto)

@Serializable
private data class ResolveVanityDto(val success: Int, val steamid: String? = null)

@Serializable
private data class PlayerSummariesResponseDto(val response: PlayersDto)

@Serializable
private data class PlayersDto(val players: List<PlayerDto>)

@Serializable
private data class PlayerDto(
    @SerialName("steamid") val steamId: String,
    @SerialName("avatarfull") val avatar: String,
    @SerialName("personaname") val personaName: String,
    @SerialName("personastate") val personaState: Int,
    @SerialName("lastlogoff") val lastLogoff: Long? = null,
    @SerialName("gameid") val gameId: String? = null,
)

class SteamRepositoryImpl(
    private val httpClient: HttpClient,
    json: Json,
) : SteamRepository, BaseApiResponse(json) {
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun searchUser(apiKey: String, query: String): Flow<Result<SteamUser?>> {
        return if (query.all { it.isDigit() }) {
            getPlayer(apiKey, query)
        } else {
            resolveVanity(apiKey, query).flatMapLatest { result ->
                when (result) {
                    is Result.Success -> getPlayer(apiKey, result.data)
                    is Result.Error -> flowOf(Result.Error(result.exception))
                }
            }
        }
    }

    private fun resolveVanity(apiKey: String, name: String): Flow<Result<String>> {
        return safeApiCall<ResolveVanityResponseDto> {
            httpClient.get("https://api.steampowered.com/ISteamUser/ResolveVanityURL/v1/") {
                parameter("key", apiKey)
                parameter("vanityurl", name)
            }
        }.map { result ->
            when (result) {
                is Result.Success -> {
                    val steamId = result.data.response.steamid
                    if (steamId != null) Result.Success(steamId)
                    else Result.Error(Exception("Not found"))
                }
                is Result.Error -> result
            }
        }
    }

    private fun getPlayer(apiKey: String, steamId: String): Flow<Result<SteamUser?>> {
        return safeApiCall<PlayerSummariesResponseDto> {
            httpClient.get("https://api.steampowered.com/ISteamUser/GetPlayerSummaries/v2/") {
                parameter("key", apiKey)
                parameter("steamids", steamId)
            }
        }.map { result ->
            when (result) {
                is Result.Success -> {
                    val player = result.data.response.players.firstOrNull()
                    Result.Success(player?.toSteamUser())
                }
                is Result.Error -> result
            }
        }
    }

    private fun PlayerDto.toSteamUser() = SteamUser(
        steamId = steamId,
        avatar = avatar,
        personaName = personaName,
        personaState = personaState,
        lastLogoff = lastLogoff,
        gameId = gameId,
    )
}

