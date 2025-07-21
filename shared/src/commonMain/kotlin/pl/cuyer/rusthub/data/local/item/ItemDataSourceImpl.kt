package pl.cuyer.rusthub.data.local.item

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import pl.cuyer.rusthub.data.local.Queries
import pl.cuyer.rusthub.database.RustHubDatabase
import pl.cuyer.rusthub.domain.model.RustItem
import pl.cuyer.rusthub.domain.model.Looting
import pl.cuyer.rusthub.domain.model.Raiding
import pl.cuyer.rusthub.domain.model.Crafting
import pl.cuyer.rusthub.domain.model.Recycling
import pl.cuyer.rusthub.domain.repository.item.local.ItemDataSource

class ItemDataSourceImpl(
    db: RustHubDatabase,
    private val json: Json
) : ItemDataSource, Queries(db) {

    override suspend fun upsertItems(items: List<RustItem>) {
        withContext(Dispatchers.IO) {
            queries.transaction {
                items.forEach { item ->
                    queries.upsertItem(
                        id = item.id ?: item.slug ?: "",
                        slug = item.slug,
                        url = item.url,
                        name = item.name,
                        description = item.description,
                        image = item.image,
                        stackSize = item.stackSize?.toLong(),
                        health = item.health?.toLong(),
                        categories = item.categories?.joinToString(",") { it.name },
                        shortName = item.shortName,
                        iconUrl = item.iconUrl,
                        language = item.language?.name,
                        looting = item.looting?.let {
                            json.encodeToString(ListSerializer(Looting.serializer()), it)
                        },
                        crafting = item.crafting?.let { json.encodeToString(it) },
                        recycling = item.recycling?.let { json.encodeToString(it) },
                        raiding = item.raiding?.let {
                            json.encodeToString(ListSerializer(Raiding.serializer()), it)
                        }
                    )
                }
            }
        }
    }

    override suspend fun isEmpty(): Boolean {
        return withContext(Dispatchers.IO) {
            queries.countItems().executeAsOne() == 0L
        }
    }
}
