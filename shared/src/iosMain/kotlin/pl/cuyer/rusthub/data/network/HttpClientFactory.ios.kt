package pl.cuyer.rusthub.data.network

import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.compression.ContentEncoding
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
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.encodedPath
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.coroutines.CancellationException
import pl.cuyer.rusthub.data.network.MutexSharedDeferred
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.data.network.auth.model.RefreshRequest
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.data.network.AppCheckPlugin
import pl.cuyer.rusthub.data.network.ForbiddenResponsePlugin
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.util.BuildType
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.util.TokenRefresher
import pl.cuyer.rusthub.data.network.CrashReportingPlugin
import pl.cuyer.rusthub.util.CrashReporter
import platform.Foundation.NSLocale
import platform.Foundation.currentLocale
import platform.Foundation.languageCode
import platform.Foundation.preferredLanguages

private fun preferredLanguageCode(): String {
    val first = (preferredLanguages.firstOrNull() as? String)
    return first ?: NSLocale.currentLocale.languageCode ?: "en"
}

actual class HttpClientFactory actual constructor(
    private val json: Json,
    private val authDataSource: AuthDataSource,
    private val appCheckTokenProvider: AppCheckTokenProvider,
    private val tokenRefresher: TokenRefresher,
    private val userEventController: UserEventController
) {
    private val refreshRunner = MutexSharedDeferred<BearerTokens?>()
    private val deleteRunner = MutexSharedDeferred<Unit>()
    actual fun create(): HttpClient {
        return HttpClient(Darwin) {

            install(ContentEncoding) {
                gzip(1.0f)
                deflate(0.9f)
            }

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
                        refreshRunner.run {
                            val user = authDataSource.getUserOnce()
                            val oldRefresh = oldTokens?.refreshToken
                            if (user?.provider == AuthProvider.ANONYMOUS || oldRefresh.isNullOrBlank()) {
                                logoutOnce()
                                return@run null
                            }

                            val currentRefresh = user?.refreshToken
                            if (currentRefresh != oldRefresh) {
                                return@run user?.let {
                                    BearerTokens(it.accessToken, currentRefresh ?: "")
                                }
                            }

                            val response = client.post("${NetworkConstants.BASE_URL}auth/refresh") {
                                markAsRefreshTokenRequest()
                                contentType(ContentType.Application.Json)
                                setBody(RefreshRequest(oldRefresh))
                            }

                            if (response.status.isSuccess()) {
                                val newTokens: TokenPairDto = response.body()
                                val confirmed = newTokens.emailConfirmed
                                    ?: authDataSource.getUserOnce()?.emailConfirmed
                                    ?: false
                                authDataSource.insertUser(
                                    email = newTokens.email,
                                    username = newTokens.username,
                                    accessToken = newTokens.accessToken,
                                    refreshToken = newTokens.refreshToken,
                                    obfuscatedId = newTokens.obfuscatedId,
                                    provider = AuthProvider.valueOf(newTokens.provider),
                                    subscribed = newTokens.subscribed,
                                    emailConfirmed = confirmed
                                )
                                BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                            } else {
                                logoutOnce()
                                null
                            }
                        }
                    }
                    sendWithoutRequest { request ->
                        val path = request.url.encodedPath
                        path.startsWith("/auth") && path !in setOf("/auth/logout", "/auth/delete")
                    }
                }
            }
            if (BuildType.isDebug) {
                install(Logging) {
                    logger = Logger.DEFAULT
                    level = LogLevel.HEADERS
                }
            }

            install(AppCheckPlugin) {
                provider = appCheckTokenProvider
            }

            install(CrashReportingPlugin)

            install(ForbiddenResponsePlugin) {
                authDataSource = this@HttpClientFactory.authDataSource
                userEventController = this@HttpClientFactory.userEventController
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            defaultRequest {
                header("Accept-Language", preferredLanguageCode())
                contentType(ContentType.Application.Json)
            }
        }

    }

    private suspend fun logoutOnce() {
        try {
            deleteRunner.run {
                if (authDataSource.getUserOnce() != null) {
                    authDataSource.deleteUser()
                    withContext(Dispatchers.Main.immediate) {
                        userEventController.sendEvent(UserEvent.LoggedOut)
                    }
                }
            }
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            CrashReporter.recordException(e)
        }
    }

}
