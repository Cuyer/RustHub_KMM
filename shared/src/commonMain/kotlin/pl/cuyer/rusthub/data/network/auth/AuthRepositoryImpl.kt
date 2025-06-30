package pl.cuyer.rusthub.data.network.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.auth.model.AccessTokenDto
import pl.cuyer.rusthub.data.network.auth.model.DeleteAccountRequest
import pl.cuyer.rusthub.data.network.auth.model.LoginRequest
import pl.cuyer.rusthub.data.network.auth.model.RefreshRequest
import pl.cuyer.rusthub.data.network.auth.model.RegisterRequest
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.auth.model.UpgradeRequest
import pl.cuyer.rusthub.data.network.auth.model.mapper.toDomain
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.AccessToken
import pl.cuyer.rusthub.domain.model.TokenPair
import pl.cuyer.rusthub.domain.repository.auth.AuthRepository

class AuthRepositoryImpl(
    private val httpClient: HttpClient,
    json: Json
) : AuthRepository, BaseApiResponse(json) {
    override fun register(
        email: String,
        password: String,
        username: String
    ): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/register") {
                setBody(RegisterRequest(username, password, email))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun login(
        username: String,
        password: String
    ): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/login") {
                setBody(LoginRequest(username, password))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun refresh(refreshToken: String): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/refresh") {
                setBody(RefreshRequest(refreshToken))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun upgrade(
        email: String,
        username: String,
        password: String
    ): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/upgrade") {
                setBody(UpgradeRequest(email, username, password))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun authAnonymously(): Flow<Result<AccessToken>> {
        return safeApiCall<AccessTokenDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/anonymous")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun logout(): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/logout")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
                Result.Loading -> Result.Loading
            }
        }
    }

    override fun deleteAccount(username: String, password: String): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/delete") {
                setBody(DeleteAccountRequest(username, password))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
                Result.Loading -> Result.Loading
            }
        }
    }
}
