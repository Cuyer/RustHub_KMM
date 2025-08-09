package pl.cuyer.rusthub.domain.repository.item

import pl.cuyer.rusthub.common.Result
import pl.cuyer.rusthub.domain.model.RustItem

interface ItemRepository {
    suspend fun getItems(): Result<List<RustItem>>
}
