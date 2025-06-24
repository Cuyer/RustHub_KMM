package pl.cuyer.rusthub.data.network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.model.ErrorResponse
import pl.cuyer.rusthub.domain.exception.AnonymousUpgradeException
import pl.cuyer.rusthub.domain.exception.FavoriteLimitException
import pl.cuyer.rusthub.domain.exception.FiltersOptionsException
import pl.cuyer.rusthub.domain.exception.InvalidCredentialsException
import pl.cuyer.rusthub.domain.exception.InvalidRefreshTokenException
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.ServersQueryException
import pl.cuyer.rusthub.domain.exception.TimeoutException
import pl.cuyer.rusthub.domain.exception.UserAlreadyExistsException
import kotlin.coroutines.coroutineContext

abstract class BaseApiResponse(
    val json: Json
) {

    inline fun <reified T> safeApiCall(crossinline apiCall: suspend () -> HttpResponse): Flow<Result<T>> =
        flow {
            coroutineContext.ensureActive()

            val response = apiCall()
            if (response.status.isSuccess()) {
                val data: T = response.body()
                emit(success(data))
            } else {
                val errorResponse = json.decodeFromString<ErrorResponse>(response.bodyAsText())
                emit(Result.Error(parseException(errorResponse)))
            }
        }.onStart {
            emit(loading())
        }.catch { e ->
            emit(error(parseConnectivityException(e)))
        }

    fun loading(): Result.Loading = Result.Loading
    fun <T> success(success: T): Result.Success<T> = Result.Success(success)
    fun <T> error(exception: Throwable): Result<T> =
        Result.Error(exception = exception)

    fun parseException(errorResponse: ErrorResponse): Throwable {
        return when (errorResponse.cause) {
            UserAlreadyExistsException::class.simpleName -> UserAlreadyExistsException(
                errorResponse.message ?: "User already exists"
            )

            InvalidCredentialsException::class.simpleName -> InvalidCredentialsException(
                errorResponse.message ?: "Invalid credentials"
            )

            InvalidRefreshTokenException::class.simpleName -> InvalidRefreshTokenException(
                errorResponse.message ?: "Invalid refresh token"
            )

            AnonymousUpgradeException::class.simpleName -> AnonymousUpgradeException(
                errorResponse.message ?: "Anonymous upgrade required"
            )

            ServersQueryException::class.simpleName -> ServersQueryException(
                errorResponse.message ?: "Servers query error"
            )

            FiltersOptionsException::class.simpleName -> FiltersOptionsException(
                errorResponse.message ?: "Filters options error"
            )

            FavoriteLimitException::class.simpleName -> FavoriteLimitException(
                errorResponse.message ?: "Favorite limit error"
            )
            else -> Exception(errorResponse.message)
        }
    }

    fun parseConnectivityException(throwable: Throwable): Throwable {
        return when (throwable) {
            is IOException -> NetworkUnavailableException(throwable.message ?: "Network unavailable")
            is TimeoutCancellationException -> TimeoutException(throwable.message ?: "Request timed out")
            else -> throwable
        }
    }
}