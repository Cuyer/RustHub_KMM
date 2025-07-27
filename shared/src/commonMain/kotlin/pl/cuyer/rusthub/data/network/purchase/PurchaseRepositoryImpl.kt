package pl.cuyer.rusthub.data.network.purchase

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository

@Serializable
private data class PurchaseRequest(
    val token: String,
    val productId: String? = null
)

class PurchaseRepositoryImpl(
    private val httpClient: HttpClient,
    json: Json
) : PurchaseRepository, BaseApiResponse(json) {
    override fun confirmPurchase(token: String, productId: String?): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "billing/confirm") {
                setBody(PurchaseRequest(token, productId))
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
            }
        }
    }
}
