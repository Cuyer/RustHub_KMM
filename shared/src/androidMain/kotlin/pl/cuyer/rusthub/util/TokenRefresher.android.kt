package pl.cuyer.rusthub.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider

actual class TokenRefresher actual constructor(private val httpClient: HttpClient) {
    actual fun clear() {
        httpClient.authProviders
            .filterIsInstance<BearerAuthProvider>()
            .forEach { it.clearToken() }
    }
}

