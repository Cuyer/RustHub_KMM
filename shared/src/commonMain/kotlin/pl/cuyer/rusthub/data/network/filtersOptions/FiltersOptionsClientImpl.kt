package pl.cuyer.rusthub.data.network.filtersOptions

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.data.network.filtersOptions.mapper.toDomain
import pl.cuyer.rusthub.data.network.filtersOptions.model.FiltersOptionsDto
import pl.cuyer.rusthub.data.network.util.BaseApiResponse
import pl.cuyer.rusthub.data.network.util.NetworkConstants
import pl.cuyer.rusthub.domain.model.FiltersOptions
import pl.cuyer.rusthub.domain.repository.filtersOptions.FiltersOptionsRepository

class FiltersOptionsClientImpl(
    private val httpClient: HttpClient,
    json: Json
) : FiltersOptionsRepository, BaseApiResponse(json) {
    override fun getFiltersOptions(): Flow<Result<FiltersOptions>> {
        return safeApiCall<FiltersOptionsDto> {
            httpClient.get(NetworkConstants.BASE_URL + "filters/options")
        }.map { result ->
            when (result) {
                is Result.Success -> Result.Success(result.data.toDomain())
                is Result.Error -> result
            }
        }
    }
}