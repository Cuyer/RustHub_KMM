package pl.cuyer.rusthub.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

actual class TokenRefresher(private val httpClient: HttpClient) {
    actual fun clear() {
        httpClient.plugin(Auth).providers
            .filterIsInstance<BearerAuthProvider>()
            .forEach { it.clearToken() }
    }
}

