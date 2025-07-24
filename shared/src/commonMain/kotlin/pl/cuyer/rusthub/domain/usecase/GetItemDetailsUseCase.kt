package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.model.Language

class GetItemDetailsUseCase(
    private val dataSource: ItemDataSource,
) {
    operator fun invoke(id: Long, language: Language): Flow<RustItem?> {
        // If the language is Polish, we switch to English for the query
        // This is a workaround for the issue with Polish language not being supported in the API
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }

        return dataSource.getItemById(id, updatedLanguage)
    }
}
