package pl.cuyer.rusthub.domain.usecase

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.local.mapper.toMonument
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.MonumentType
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource

class GetPagedMonumentsUseCase(
    private val dataSource: MonumentDataSource,
    private val json: Json,
) {
    /**
     * Invokes the use case to retrieve a paginated flow of `Monument` objects.
     *
     * @param query The optional search query to filter monuments by name.
     * @param type The optional type to monuments items.
     * @param language The language to filter monuments. If Polish is provided, it is replaced with English.
     * @return A `Flow` emitting paginated data of `Monument` objects.
     */

    operator fun invoke(
        query: String?,
        type: MonumentType?,
        language: Language,
    ): Flow<PagingData<Monument>> {
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }

        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = {
                dataSource.getMonumentsPagingSource(query, type, updatedLanguage)
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toMonument(json) }
        }
    }
}
