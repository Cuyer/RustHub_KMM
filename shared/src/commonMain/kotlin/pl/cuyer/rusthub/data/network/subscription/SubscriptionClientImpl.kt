package pl.cuyer.rusthub.data.network.subscription

import io.ktor.client.HttpClient
import io.ktor.client.request.delete
import io.ktor.client.request.post
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.repository.subscription.network.SubscriptionRepository

class SubscriptionClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : SubscriptionRepository, BaseApiResponse(json) {
    override fun addSubscription(id: Long): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.post(NetworkConstants.BASE_URL + "subscriptions/$id")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
            }
        }
    }

    override fun removeSubscription(id: Long): Flow<Result<Unit>> {
        return safeApiCall<Unit> {
            httpClient.delete(NetworkConstants.BASE_URL + "subscriptions/$id")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(Unit)
                is Result.Error -> result
            }
        }
    }
}
