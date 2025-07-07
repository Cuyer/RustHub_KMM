package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource

expect class HttpClientFactory(
    json: Json,
    authDataSource: AuthDataSource
) {

    fun create(): HttpClient
}