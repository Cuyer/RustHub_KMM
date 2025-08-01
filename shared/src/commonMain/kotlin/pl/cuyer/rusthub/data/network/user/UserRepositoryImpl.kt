package pl.cuyer.rusthub.data.network.user

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.user.mapper.toDomain
import pl.cuyer.rusthub.data.network.user.model.UserInfoDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.User
import pl.cuyer.rusthub.domain.model.UserStatus
import pl.cuyer.rusthub.domain.repository.user.UserRepository



class UserRepositoryImpl(
    private val httpClient: HttpClient,
    json: Json,
) : UserRepository, BaseApiResponse(json) {
    override fun getUserStatus(): Flow<Result<UserStatus>> {
        return safeApiCall<UserInfoDto> {
            httpClient.get(NetworkConstants.BASE_URL + "me")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(
                    UserStatus(
                        emailConfirmed = result.data.emailConfirmed,
                        subscribed = result.data.subscribed,
                    )
                )
                is Result.Error -> result
            }
        }
    }

    override fun resendConfirmation(): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/resend-confirmation")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
            }
        }
    }

    override fun getUser(): Flow<Result<User>> {
        return safeApiCall<UserInfoDto> {
            httpClient.get(NetworkConstants.BASE_URL + "me")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
            }
        }
    }
}

