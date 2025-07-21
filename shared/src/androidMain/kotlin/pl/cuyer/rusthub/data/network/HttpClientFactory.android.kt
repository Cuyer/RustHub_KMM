package pl.cuyer.rusthub.data.network

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
import android.os.Build
import com.appmattus.certificatetransparency.certificateTransparencyInterceptor
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
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
import io.ktor.http.HttpMethod
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.io.IOException
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.network.auth.model.RefreshRequest
import pl.cuyer.rusthub.data.network.auth.model.TokenPairDto
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.data.network.AppCheckPlugin
import pl.cuyer.rusthub.util.AppCheckTokenProvider
import pl.cuyer.rusthub.util.TokenRefresher
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import pl.cuyer.rusthub.util.BuildType
import pl.cuyer.rusthub.domain.model.AuthProvider
import pl.cuyer.rusthub.domain.repository.auth.AuthDataSource
import java.util.Locale
import androidx.appcompat.app.AppCompatDelegate
import kotlin.random.Random

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
    private val tokenRefresher: TokenRefresher
) {
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
                            authDataSource.deleteUser()
                            tokenRefresher.clear()
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