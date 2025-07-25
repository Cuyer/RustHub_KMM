package pl.cuyer.rusthub.util

import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.authProviders
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class TokenRefresher: KoinComponent {
    private val httpClient: HttpClient by inject()
    fun clear() {
        httpClient.authProviders
            .filterIsInstance<BearerAuthProvider>()
            .forEach { it.clearToken() }
    }
}

