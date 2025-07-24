package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.util.TokenRefresher

expect class HttpClientFactory(
    json: Json,
    authDataSource: AuthDataSource,
    appCheckTokenProvider: AppCheckTokenProvider,
    tokenRefresher: TokenRefresher,
    userEventController: UserEventController
) {
    fun create(): HttpClient
}