package pl.cuyer.rusthub.data.network.notification

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import pl.cuyer.rusthub.data.network.notification.model.FcmTokenRequest
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.notification.MessagingTokenRepository

class MessagingTokenClientImpl(
    private val httpClient: HttpClient
) : MessagingTokenRepository {
    override suspend fun registerToken(token: String) {
        runCatching {
            httpClient.post(NetworkConstants.BASE_URL + "fcm/token") {
                setBody(FcmTokenRequest(token))
            }
        }
    }
}
