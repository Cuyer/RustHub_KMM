package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import kotlinx.serialization.json.Json

expect class HttpClientFactory(json: Json) {

    fun create(): HttpClient
}