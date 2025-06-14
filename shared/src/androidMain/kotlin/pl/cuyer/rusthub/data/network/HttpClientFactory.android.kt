package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import java.util.Locale

actual class HttpClientFactory actual constructor(
    private val json: Json
) {

    actual fun create(): HttpClient {
        return HttpClient(OkHttp) {
            install(ContentNegotiation) {
                json
            }
            install(Logging) {
                logger = Logger.SIMPLE
                level = LogLevel.ALL
            }

            defaultRequest {
                header("Accept-Language", Locale.getDefault().toLanguageTag())
                contentType(ContentType.Application.Json)
            }
        }

    }

}