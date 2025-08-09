package pl.cuyer.rusthub.data.network.item

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
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

    override suspend fun getItems(): Result<List<RustItem>> {
        val firstPage = when (val res = safeApiCall<ItemsResponseDto> {
            httpClient.get(NetworkConstants.BASE_URL + "items") {
                url {
                    parameters.append("page", "0")
                    parameters.append("size", PAGE_SIZE.toString())
                }
            }
        }.first()) {
            is Result.Success -> res.data.toDomain()
            is Result.Error -> return res
        }

        val items = firstPage.items.toMutableList()

        if (firstPage.totalPages > 1) {
            val results = (1 until firstPage.totalPages).map { page ->
                httpClient.async {
                    safeApiCall<ItemsResponseDto> {
                        httpClient.get(NetworkConstants.BASE_URL + "items") {
                            url {
                                parameters.append("page", page.toString())
                                parameters.append("size", PAGE_SIZE.toString())
                            }
                        }
                    }.first()
                }
            }.awaitAll()

            for (res in results) {
                when (res) {
                    is Result.Success -> items += res.data.toDomain().items
                    is Result.Error -> return res
                }
            }
        }

        return Result.Success(items)
    }
}
