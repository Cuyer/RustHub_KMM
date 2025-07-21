package pl.cuyer.rusthub.domain.repository.item.local

import pl.cuyer.rusthub.domain.model.RustItem

interface ItemDataSource {
    suspend fun upsertItems(items: List<RustItem>)
    suspend fun isEmpty(): Boolean
}
