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
    operator fun invoke(
        query: String?,
        type: MonumentType?,
        language: Language,
    ): Flow<PagingData<Monument>> {
        return Pager(
            config = PagingConfig(
                pageSize = 30,
                enablePlaceholders = true,
            ),
            pagingSourceFactory = {
                dataSource.getMonumentsPagingSource(query, type, language)
            },
        ).flow.map { pagingData ->
            pagingData.map { it.toMonument(json) }
        }
    }
}
