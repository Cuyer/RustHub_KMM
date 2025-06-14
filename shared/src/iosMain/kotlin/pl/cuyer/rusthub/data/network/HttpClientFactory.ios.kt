package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import kotlinx.serialization.json.Json
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import io.ktor.client.engine.darwin.Darwin
import io.ktor.http.ContentType
import io.ktor.http.contentType

actual class HttpClientFactory actual constructor(
    private val json: Json
) {
    actual fun create(): HttpClient {
        return HttpClient(Darwin) {
            install(ContentNegotiation) {
                json
            }
            install(Logging) {
                logger = Logger.DEFAULT
                level = LogLevel.ALL
            }

            defaultRequest {
                header("Accept-Language", NSLocale.currentLocale.languageCode)
                contentType(ContentType.Application.Json)
            }
        }

    }

}