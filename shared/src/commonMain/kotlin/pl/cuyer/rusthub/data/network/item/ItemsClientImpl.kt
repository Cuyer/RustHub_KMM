package pl.cuyer.rusthub.data.network.item

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.item.mapper.toDomain
import pl.cuyer.rusthub.data.network.item.model.ItemsResponseDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.ItemRepository

class ItemsClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : ItemRepository, BaseApiResponse(json) {
    override fun getItems(): Flow<Result<List<RustItem>>> {
        return safeApiCall<ItemsResponseDto> {
            httpClient.get(NetworkConstants.BASE_URL + "items")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.items.map { it.toDomain() })
                is Result.Error -> result
            }
        }
    }
}
