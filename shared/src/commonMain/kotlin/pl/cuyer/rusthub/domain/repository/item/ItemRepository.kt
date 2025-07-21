package pl.cuyer.rusthub.domain.repository.item

import kotlinx.coroutines.flow.Flow
import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.RustItem

interface ItemRepository {
    fun getItems(): Flow<Result<List<RustItem>>>
}
