package pl.cuyer.rusthub.domain.repository.monument.local

import androidx.paging.PagingSource
import database.MonumentEntity
import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.MonumentType

interface MonumentDataSource {
    suspend fun upsertMonuments(monuments: List<Monument>)
    suspend fun isEmpty(language: Language): Boolean
    fun getMonumentsPagingSource(
        name: String?,
        type: MonumentType?,
        language: Language,
    ): PagingSource<Int, MonumentEntity>
    fun getMonumentBySlug(slug: String, language: Language): Flow<Monument?>
}
