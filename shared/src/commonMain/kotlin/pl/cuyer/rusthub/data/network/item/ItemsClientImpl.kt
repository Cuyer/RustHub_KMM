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

    private companion object { const val PAGE_SIZE = 100 }

    override fun getItems(): Flow<Result<List<RustItem>>> = flow {
        var page = 0
        var totalPages = Int.MAX_VALUE

        while (page < totalPages) {
            when (val res = safeApiCall<ItemsResponseDto> {
                httpClient.get(NetworkConstants.BASE_URL + "items") {
                    url {
                        parameters.append("page", page.toString())
                        parameters.append("size", PAGE_SIZE.toString())
                    }
                }
            }.first()) {
                is Result.Success -> {
                    val response = res.data.toDomain()
                    emit(Result.Success(response.items)) // one page
                    totalPages = response.totalPages
                    page++
                    if (response.items.isEmpty()) break // safety
                }
                is Result.Error -> {
                    emit(res)
                    return@flow
                }
            }
        }
    }
}
