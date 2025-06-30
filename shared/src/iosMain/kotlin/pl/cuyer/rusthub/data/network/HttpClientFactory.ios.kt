package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.network.auth.model.RefreshRequest
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode

actual class HttpClientFactory actual constructor(
    private val json: Json,
    private val authDataSource: AuthDataSource
) {
    actual fun create(): HttpClient {
        return HttpClient(Darwin) {
            install(ContentNegotiation) {
                json(json)
            }
            install(Auth) {
                bearer {
                    loadTokens {
                        authDataSource.getUserOnce()?.let {
                            BearerTokens(it.accessToken, it.refreshToken ?: "")
                        }
                    }
                    refreshTokens {
                        val oldRefresh = oldTokens?.refreshToken ?: return@refreshTokens null

                        val response = client.post("${NetworkConstants.BASE_URL}auth/refresh") {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshRequest(oldRefresh))
                        }

                        if (response.status.isSuccess()) {
                            val newTokens: TokenPairDto = response.body()
                            authDataSource.insertUser(
                                email = newTokens.email,
                                username = newTokens.username,
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken
                            )
                            BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                        } else null
                    }
                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath
                        path.startsWith("/auth") && path !in setOf("/auth/logout", "/auth/delete")
                    }
                }
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