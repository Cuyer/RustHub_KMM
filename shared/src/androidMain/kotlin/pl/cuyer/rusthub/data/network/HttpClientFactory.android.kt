package pl.cuyer.rusthub.data.network

import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import com.appmattus.certificatetransparency.certificateTransparencyInterceptor
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.user.UserEvent
import pl.cuyer.rusthub.data.network.auth.model.RefreshRequest
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import pl.cuyer.rusthub.common.user.UserEventController
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.util.BuildType
import pl.cuyer.rusthub.util.TokenRefresher
import java.util.Locale

private fun useCtLibrary(): Boolean {
    return Build.VERSION.SDK_INT < 36
}

private fun currentLanguageTag(): String {
    val locales = AppCompatDelegate.getApplicationLocales()
    val locale = if (!locales.isEmpty) {
        locales[0]
    } else {
        Locale.getDefault()
    }
    return locale?.toLanguageTag() ?: Locale.getDefault().toLanguageTag()
}

actual class HttpClientFactory actual constructor(
    private val json: Json,
    private val authDataSource: AuthDataSource,
    private val appCheckTokenProvider: AppCheckTokenProvider,
    private val tokenRefresher: TokenRefresher,
    private val userEventController: UserEventController
)  {
    actual fun create(): HttpClient {
        return HttpClient(OkHttp) {
            engine {
                if (useCtLibrary()) {
                    addNetworkInterceptor(certificateTransparencyInterceptor())
                }
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
                        val oldRefresh = oldTokens?.refreshToken ?: return@refreshTokens null

                        val response = client.post("${NetworkConstants.BASE_URL}auth/refresh") {
                            markAsRefreshTokenRequest()
                            contentType(ContentType.Application.Json)
                            setBody(RefreshRequest(oldRefresh))
                        }

                        if (response.status.isSuccess()) {
                            val newTokens: TokenPairDto = response.body()
                            val confirmed = authDataSource.getUserOnce()?.emailConfirmed ?: false
                            authDataSource.insertUser(
                                email = newTokens.email,
                                username = newTokens.username,
                                accessToken = newTokens.accessToken,
                                refreshToken = newTokens.refreshToken,
                                provider = AuthProvider.valueOf(newTokens.provider),
                                subscribed = newTokens.subscribed,
                                emailConfirmed = confirmed
                            )
                            BearerTokens(newTokens.accessToken, newTokens.refreshToken)
                        } else {
                            try {
                                if (authDataSource.getUserOnce() != null) {
                                    authDataSource.deleteUser()
                                    withContext(Dispatchers.Main.immediate) {
                                        userEventController.sendEvent(UserEvent.LoggedOut)
                                    }
                                }
                            } catch (e: Exception) {
                                Napier.e(message = "Failed to delete user on token refresh failure", throwable = e)
                            }
                            null
                        }
                    }
                }
            }
            if (BuildType.isDebug) {
                install(Logging) {
                    logger = Logger.SIMPLE
                    level = LogLevel.ALL
                }
            }

            install(AppCheckPlugin) {
                provider = appCheckTokenProvider
            }

            install(ForbiddenResponsePlugin) {
                authDataSource = this@HttpClientFactory.authDataSource
                userEventController = this@HttpClientFactory.userEventController
            }

            install(HttpRequestRetry) {
                retryOnServerErrors(maxRetries = 3)
                exponentialDelay()
            }

            defaultRequest {
                header("Accept-Language", currentLanguageTag())
                contentType(ContentType.Application.Json)
            }
        }

    }

}