package pl.cuyer.rusthub.data.network.util

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.io.IOException
import kotlinx.serialization.SerializationException
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

object ApiExceptionMapper {
    fun fromErrorResponse(errorResponse: ErrorResponse): Throwable {
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

    fun fromStatusCode(statusCode: Int): HttpStatusException {
        return when (statusCode) {
            401 -> UnauthorizedException("You are not authorized. Please log in again.")
            403 -> ForbiddenException("You don't have permission to perform this action.")
            404 -> NotFoundException("Resource not found.")
            429 -> TooManyRequestsException("You’re doing that too much. Please wait a moment.")
            503 -> ServiceUnavailableException("Service temporarily unavailable. Try again later.")
            else -> HttpStatusException("HTTP $statusCode error")
        }
    }

    fun fromThrowable(throwable: Throwable): Throwable {
        return when (throwable) {
            is ResponseException -> fromStatusCode(throwable.response.status.value)
            is ConnectTimeoutException -> TimeoutException("Connection timed out")
            is SocketTimeoutException -> TimeoutException("Connection timed out")
            is TimeoutCancellationException -> TimeoutException("Request timed out")
            is SerializationException -> SerializationException("Invalid response format")
            // General IO
            is IOException -> NetworkUnavailableException("Network error. Please check your connection.")

            else -> {
                throwable
            }
        }
    }
}