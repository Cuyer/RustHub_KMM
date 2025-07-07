package pl.cuyer.rusthub.data.network.user

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.user.UserRepository

@Serializable
private data class UserDto(val emailConfirmed: Boolean)

class UserRepositoryImpl(
    private val httpClient: HttpClient,
    json: Json,
) : UserRepository, BaseApiResponse(json) {
    override fun isEmailConfirmed(): Flow<Result<Boolean>> {
        return safeApiCall<UserDto> {
            httpClient.get(NetworkConstants.BASE_URL + "me")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.emailConfirmed)
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun resendConfirmation(): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "resend-confirmation")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
                Result.Loading -> Result.Loading
            }
        }
    }
}
