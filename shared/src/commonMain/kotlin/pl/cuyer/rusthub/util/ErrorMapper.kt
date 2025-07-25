package pl.cuyer.rusthub.util

import pl.cuyer.rusthub.SharedRes
import pl.cuyer.rusthub.domain.exception.AnonymousUpgradeException
import pl.cuyer.rusthub.domain.exception.BadRequestException
import pl.cuyer.rusthub.domain.exception.FavoriteLimitException
import pl.cuyer.rusthub.domain.exception.FiltersOptionsException
import pl.cuyer.rusthub.domain.exception.ForbiddenException
import pl.cuyer.rusthub.domain.exception.HttpStatusException
import pl.cuyer.rusthub.domain.exception.InternalServerErrorException
import pl.cuyer.rusthub.domain.exception.InvalidCredentialsException
import pl.cuyer.rusthub.domain.exception.InvalidRefreshTokenException
import pl.cuyer.rusthub.domain.exception.NetworkUnavailableException
import pl.cuyer.rusthub.domain.exception.NotFoundException
import pl.cuyer.rusthub.domain.exception.ServiceUnavailableException
import pl.cuyer.rusthub.domain.exception.ServersQueryException
import pl.cuyer.rusthub.domain.exception.SubscriptionLimitException
import pl.cuyer.rusthub.domain.exception.TimeoutException
import pl.cuyer.rusthub.domain.exception.TooManyRequestsException
import pl.cuyer.rusthub.domain.exception.UnauthorizedException
import pl.cuyer.rusthub.domain.exception.UserAlreadyExistsException

fun Throwable.toUserMessage(stringProvider: StringProvider): String? {
    return when (this) {
        is InvalidCredentialsException -> stringProvider.get(SharedRes.strings.provided_credentials_incorrect)
        is UserAlreadyExistsException -> stringProvider.get(SharedRes.strings.user_already_exists)
        is InvalidRefreshTokenException -> stringProvider.get(SharedRes.strings.error_invalid_refresh_token)
        is AnonymousUpgradeException -> stringProvider.get(SharedRes.strings.provide_credentials_or_connect_google_account_to_upgrade)
        is NetworkUnavailableException -> stringProvider.get(SharedRes.strings.error_network)
        is TimeoutException -> stringProvider.get(SharedRes.strings.error_timeout)
        is ServiceUnavailableException -> stringProvider.get(SharedRes.strings.error_service_unavailable)
        is BadRequestException -> stringProvider.get(SharedRes.strings.error_bad_request)
        is InternalServerErrorException -> stringProvider.get(SharedRes.strings.error_server)
        is UnauthorizedException -> stringProvider.get(SharedRes.strings.error_unauthorized)
        is ForbiddenException -> stringProvider.get(SharedRes.strings.error_forbidden)
        is NotFoundException -> stringProvider.get(SharedRes.strings.error_not_found)
        is TooManyRequestsException -> stringProvider.get(SharedRes.strings.wait_before_resending)
        is FiltersOptionsException -> stringProvider.get(SharedRes.strings.error_fetching_filters)
        is ServersQueryException -> stringProvider.get(SharedRes.strings.error_fetching_servers)
        is FavoriteLimitException, is SubscriptionLimitException -> null
        is HttpStatusException -> stringProvider.get(SharedRes.strings.error_unknown)
        else -> this.message ?: stringProvider.get(SharedRes.strings.error_unknown)
    }
}
