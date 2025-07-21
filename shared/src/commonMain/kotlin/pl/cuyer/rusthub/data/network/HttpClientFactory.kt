package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.util.AppCheckTokenProvider

expect class HttpClientFactory(
    json: Json,
    authDataSource: AuthDataSource,
    appCheckTokenProvider: AppCheckTokenProvider
) {
    fun create(): HttpClient
}