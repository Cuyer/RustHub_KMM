package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource

class GetMonumentDetailsUseCase(
    private val dataSource: MonumentDataSource,
) {
    operator fun invoke(slug: String, language: Language): Flow<Monument?> {
        return dataSource.getMonumentBySlug(slug, language)
    }
}
