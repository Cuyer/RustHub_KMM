package pl.cuyer.rusthub.data.network.purchase

import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.request.parameter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.purchase.PurchaseRepository
import pl.cuyer.rusthub.domain.model.ActiveSubscription
import pl.cuyer.rusthub.data.network.purchase.model.PurchaseInfoDto
import pl.cuyer.rusthub.data.network.purchase.mapper.toDomain
import io.github.aakira.napier.Napier
import pl.cuyer.rusthub.util.CrashReporter

@Serializable
private data class PurchaseRequest(val token: String, val productId: String? = null)

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

    override fun getActiveSubscription(obfuscatedId: String): Flow<Result<ActiveSubscription?>> {
        return safeApiCall<List<PurchaseInfoDto>> {
            httpClient.get(NetworkConstants.BASE_URL + "billing/purchase") {
                parameter("account", obfuscatedId)
            }
        }.map { result ->
            when (result) {
                is Result.Success -> {
                    val sub = result.data.firstNotNullOfOrNull { dto ->
                        val mapped = dto.toDomain()
                        mapped
                    }
                    Result.Success(sub)
                }
                is Result.Error -> {
                    CrashReporter.recordException(result.exception)
                    result
                }
            }
        }
    }
}
