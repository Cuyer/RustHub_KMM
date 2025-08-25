package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.ItemRepository
import pl.cuyer.rusthub.domain.model.Language

/**
 * Use case for retrieving the details of a specific `RustItem` by its ID and language.
 *
 * This class is part of the domain layer and adheres to clean architecture principles.
 * It interacts with the `ItemRepository` to fetch the item details from the network.
 * The `updatedLanguage` variable is used to handle a specific limitation where the Polish language
 * is not supported in the API. If the provided language is Polish, it is replaced with English
 * to ensure compatibility with the API.
 *
 * @property repository The repository for retrieving item details.
 */
class GetItemDetailsUseCase(
    private val repository: ItemRepository,
) {
    /**
     * Invokes the use case to retrieve the details of a `RustItem` by its id and language.
     *
     * @param id The id of the item to retrieve.
     * @param language The language to filter the item. If Polish is provided, it is replaced with English.
     * @return A `Flow` emitting a [Result] containing the `RustItem` or an error.
     */
    operator fun invoke(id: Long, language: Language): Flow<Result<RustItem>> {
        val updatedLanguage = if (language == Language.POLISH) {
            Language.ENGLISH
        } else {
            language
        }

        return repository.getItemDetails(id, updatedLanguage)
    }
}