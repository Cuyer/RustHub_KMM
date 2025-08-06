package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.Language
import pl.cuyer.rusthub.domain.model.Monument
import pl.cuyer.rusthub.domain.repository.monument.local.MonumentDataSource

class GetMonumentDetailsUseCase(
    private val dataSource: MonumentDataSource,
) {
    /**
     * Invokes the use case to retrieve the details of a `Monument` by its slug and language.
     *
     * @param slug The slug of the monument to retrieve.
     * @param language The language to filter the monument. If Polish is provided, it is replaced with English.
     * @return A `Flow` emitting the `Monument` or `null` if not found.
     */
    operator fun invoke(slug: String, language: Language): Flow<Monument?> {
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }
        return dataSource.getMonumentBySlug(slug, updatedLanguage)
    }
}
