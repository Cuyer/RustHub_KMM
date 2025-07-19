package pl.cuyer.rusthub.data.network.util

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import io.ktor.http.isSuccess
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.model.ErrorResponse
import pl.cuyer.rusthub.domain.exception.AnonymousUpgradeException
import pl.cuyer.rusthub.domain.exception.FavoriteLimitException
import pl.cuyer.rusthub.domain.exception.SubscriptionLimitException
import pl.cuyer.rusthub.domain.exception.FiltersOptionsException
import pl.cuyer.rusthub.domain.exception.ForbiddenException
import pl.cuyer.rusthub.domain.exception.HttpStatusException
import pl.cuyer.rusthub.domain.exception.InvalidCredentialsException
import pl.cuyer.rusthub.domain.exception.InvalidRefreshTokenException
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.NotFoundException
import pl.cuyer.rusthub.domain.exception.ServersQueryException
import pl.cuyer.rusthub.domain.exception.TimeoutException
import pl.cuyer.rusthub.domain.exception.UnauthorizedException
import pl.cuyer.rusthub.domain.exception.UserAlreadyExistsException
import pl.cuyer.rusthub.domain.exception.TooManyRequestsException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
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
                val body = response.bodyAsText()
                val exception = try {
                    if (body.isNotBlank()) {
                        val errorResponse = json.decodeFromString<ErrorResponse>(body)
                        parseException(errorResponse)
                    } else {
                        parseStatusCodeException(response.status.value)
                    }
                } catch (e: SerializationException) {
                    parseStatusCodeException(response.status.value)
                }
                emit(Result.Error(exception))
            }
        }.catch { e ->
            emit(error(parseConnectivityException(e)))
        }
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
            SubscriptionLimitException::class.simpleName -> SubscriptionLimitException(
                errorResponse.message ?: "Subscription limit error"
            )
            else -> Exception(errorResponse.message)
        }
    }

    fun parseStatusCodeException(statusCode: Int): HttpStatusException {
        return when (statusCode) {
            HttpStatusCode.Unauthorized.value ->
                UnauthorizedException("Unauthorized")
            HttpStatusCode.Forbidden.value ->
                ForbiddenException("Forbidden")
            HttpStatusCode.NotFound.value ->
                NotFoundException("Not found")
            HttpStatusCode.TooManyRequests.value ->
                TooManyRequestsException("Too many requests")
            HttpStatusCode.ServiceUnavailable.value ->
                ServiceUnavailableException("Service unavailable")
            else -> HttpStatusException("HTTP $statusCode error")
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