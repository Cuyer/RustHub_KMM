package pl.cuyer.rusthub.data.network.auth

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.auth.model.AccessTokenDto
import pl.cuyer.rusthub.data.network.auth.model.DeleteAccountRequest
import pl.cuyer.rusthub.data.network.auth.model.ChangePasswordRequest
import pl.cuyer.rusthub.data.network.auth.model.GoogleLoginRequest
import pl.cuyer.rusthub.data.network.auth.model.LoginRequest
import pl.cuyer.rusthub.data.network.auth.model.RefreshRequest
import pl.cuyer.rusthub.data.network.auth.model.RegisterRequest
import pl.cuyer.rusthub.data.network.auth.model.ForgotPasswordRequest
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.auth.model.UpgradeRequest
import pl.cuyer.rusthub.data.network.auth.model.UserExistsResponseDto
import pl.cuyer.rusthub.data.network.auth.model.mapper.toDomain
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.AccessToken
import pl.cuyer.rusthub.domain.model.TokenPair
import pl.cuyer.rusthub.domain.model.UserExistsInfo
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
        username: String,
        email: String,
        password: String
    ): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/upgrade") {
                setBody(
                    UpgradeRequest(
                        username = username,
                        password = password,
                        email = email,
                    )
                )
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

    override fun loginWithGoogle(token: String): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/google") {
                setBody(GoogleLoginRequest(token))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun upgradeWithGoogle(token: String): Flow<Result<TokenPair>> {
        return safeApiCall<TokenPairDto> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/upgrade") {
                setBody(UpgradeRequest(token = token))
            }
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

    override fun deleteAccount(password: String): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/delete") {
                setBody(DeleteAccountRequest(password))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
                Result.Loading -> Result.Loading
            }
        }
    }

    override fun changePassword(oldPassword: String, newPassword: String): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "auth/password") {
                setBody(ChangePasswordRequest(oldPassword, newPassword))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
                Result.Loading -> Result.Loading
            }
        }
    }

    override fun checkUserExists(email: String): Flow<Result<UserExistsInfo>> {
        return safeApiCall<UserExistsResponseDto> {
            httpClient.get(NetworkConstants.BASE_URL + "auth/email-exists") {
                parameter("email", email)
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
                is Result.Loading -> Result.Loading
            }
        }
    }

    override fun requestPasswordReset(email: String): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "forgot-password") {
                setBody(ForgotPasswordRequest(email))
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
