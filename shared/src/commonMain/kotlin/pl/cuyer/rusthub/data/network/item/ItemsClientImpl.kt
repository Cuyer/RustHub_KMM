package pl.cuyer.rusthub.data.network.item

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.item.mapper.toDomain
import pl.cuyer.rusthub.data.network.item.model.ItemsResponseDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.ItemRepository

private const val PAGE_SIZE = 100

class ItemsClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : ItemRepository, BaseApiResponse(json) {
    override fun getItems(): Flow<Result<List<RustItem>>> = flow {
        val items = mutableListOf<RustItem>()
        var page = 0
        var totalPages = 1
        do {
            when (val result = safeApiCall<ItemsResponseDto> {
                httpClient.get(NetworkConstants.BASE_URL + "items") {
                    url {
                        parameters.append("page", page.toString())
                        parameters.append("size", PAGE_SIZE.toString())
                    }
                }
            }.first()) {
                is Result.Success -> {
                    val response = result.data.toDomain()
                    items.addAll(response.items)
                    totalPages = response.totalPages
                    page++
                }
                is Result.Error -> {
                    emit(result)
                    return@flow
                }
            }
        } while (page < totalPages)
        emit(Result.Success(items))
    }
}
