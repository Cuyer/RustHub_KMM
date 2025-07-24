package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.model.Language

/**
 * Use case for retrieving the details of a specific `RustItem` by its ID and language.
 *
 * This class is part of the domain layer and adheres to clean architecture principles.
 * It interacts with the `ItemDataSource` to fetch the item details.
 * The `updatedLanguage` variable is used to handle a specific limitation where the Polish language
 * is not supported in the API. If the provided language is Polish, it is replaced with English
 * to ensure compatibility with the API.
 *
 * @property dataSource The data source for retrieving item details.
 */
class GetItemDetailsUseCase(
    private val dataSource: ItemDataSource,
) {
    /**
     * Invokes the use case to retrieve the details of a `RustItem` by its ID and language.
     *
     * @param id The ID of the item to retrieve.
     * @param language The language to filter the item. If Polish is provided, it is replaced with English.
     * @return A `Flow` emitting the `RustItem` or `null` if not found.
     */
    operator fun invoke(id: Long, language: Language): Flow<RustItem?> {
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }

        return dataSource.getItemById(id, updatedLanguage)
    }
}