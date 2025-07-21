package pl.cuyer.rusthub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource

class GetItemDetailsUseCase(
    private val dataSource: ItemDataSource,
) {
    operator fun invoke(id: Long): Flow<RustItem?> {
        return dataSource.getItemById(id)
    }
}
