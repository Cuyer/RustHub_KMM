package pl.cuyer.rusthub.data.network.item

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.item.mapper.toDomain
import pl.cuyer.rusthub.data.network.item.model.ItemsResponseDto
import pl.cuyer.rusthub.data.network.item.model.ItemDetailsResponseDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.data.network.util.appendNonNull
import pl.cuyer.rusthub.domain.model.ItemCategory
import pl.cuyer.rusthub.domain.model.ItemsResponse
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.ItemRepository

private const val PAGE_SIZE = 100

class ItemsClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : ItemRepository, BaseApiResponse(json) {

    override fun getItems(
        page: Int,
        size: Int,
        category: ItemCategory?,
        language: Language,
        searchQuery: String?
    ): Flow<Result<ItemsResponse>> {
        return safeApiCall<ItemsResponseDto> {
            httpClient.get(NetworkConstants.BASE_URL + "items") {
                url {
                    appendNonNull("page" to page)
                    appendNonNull("size" to size)
                    appendNonNull("category" to category?.name?.lowercase()?.replaceFirstChar { it.uppercase() })
                    appendNonNull("language" to language.toApiValue())
                    if (!searchQuery.isNullOrBlank()) parameters.append("name", searchQuery)
                }
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
            }
        }
    }

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

    override fun getItemDetails(slug: String, language: Language): Flow<Result<RustItem>> {
        return safeApiCall<ItemDetailsResponseDto> {
            httpClient.get(NetworkConstants.BASE_URL + "items/details") {
                url {
                    appendNonNull("slug" to slug)
                    appendNonNull("language" to language.toApiValue())
                }
            }
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.item.toDomain())
                is Result.Error -> result
            }
        }
    }

    private fun Language.toApiValue(): String = when (this) {
        Language.ENGLISH -> "en"
        Language.POLISH -> "pl"
        Language.GERMAN -> "de"
        Language.FRENCH -> "fr"
        Language.RUSSIAN -> "ru"
        Language.PORTUGUESE -> "pt"
        Language.SPANISH -> "es"
        Language.UKRAINIAN -> "uk"
    }
}
