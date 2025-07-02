package pl.cuyer.rusthub.util

import io.ktor.client.HttpClient

expect class TokenRefresher(httpClient: HttpClient) {
    fun clear()
}

