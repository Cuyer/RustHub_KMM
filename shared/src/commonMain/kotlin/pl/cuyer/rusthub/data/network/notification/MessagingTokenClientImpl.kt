package pl.cuyer.rusthub.data.network.notification

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.datetime.Instant
import pl.cuyer.rusthub.data.network.notification.model.FcmTokenRequest
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository

class MessagingTokenClientImpl(
    private val httpClient: HttpClient
) : MessagingTokenRepository {
    override suspend fun registerToken(token: String, timestamp: Instant) {
        runCatching {
            httpClient.post(NetworkConstants.BASE_URL + "fcm/token") {
                setBody(FcmTokenRequest(token, timestamp))
            }
        }
    }

    override suspend fun deleteToken(token: String) {
        runCatching {
            httpClient.delete(NetworkConstants.BASE_URL + "fcm/token/$token")
        }
    }
}
