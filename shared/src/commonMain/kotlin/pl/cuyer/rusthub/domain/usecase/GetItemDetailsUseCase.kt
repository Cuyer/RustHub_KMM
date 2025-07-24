package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource
import pl.cuyer.rusthub.domain.model.Language

class GetItemDetailsUseCase(
    private val dataSource: ItemDataSource,
) {
    operator fun invoke(id: Long, language: Language): Flow<RustItem?> {
        return dataSource.getItemById(id, language)
    }
}
